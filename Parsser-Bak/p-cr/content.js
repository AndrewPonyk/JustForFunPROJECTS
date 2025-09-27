// Chrome extension content script to handle Ctrl+Alt+Q shortcut
// Simulates Ctrl+A (select all) on editable elements
console.log("test");
document.addEventListener('keydown', function(event) {
	
    // Check for Ctrl+Alt+Q combination
    if (event.ctrlKey && event.altKey && event.key === 'q') {
        event.preventDefault();

        const activeElement = document.activeElement;
        console.log('Ctrl+Alt+Q pressed, active element1234:', activeElement);
        console.log('Active element text content:', activeElement.textContent);
        console.log('Active element value:', activeElement.value);

        // Select all, print to console, and deselect - all invisibly fast!
        console.log('Starting: Select All → Print → Deselect');
        selectPrintDeselect(activeElement);
    }
});

function isEditableElement(element) {
    if (!element) return false;

    const tagName = element.tagName.toLowerCase();
    const type = element.type ? element.type.toLowerCase() : '';

    // Standard form input elements
    if (tagName === 'textarea') return true;
    if (tagName === 'input' && ['text', 'password', 'email', 'url', 'search', 'tel'].includes(type)) return true;

    // ContentEditable elements
    if (element.contentEditable === 'true' || element.contentEditable === '') return true;

    // Elements with role="textbox"
    if (element.getAttribute('role') === 'textbox') return true;

    return false;
}

function isHiddenProxyElement(element) {
    if (!element) return false;

    const style = window.getComputedStyle(element);
    const rect = element.getBoundingClientRect();

    console.log('Checking if proxy element:', {
        position: style.position,
        visibility: style.visibility,
        opacity: style.opacity,
        height: rect.height,
        width: rect.width,
        left: style.left,
        top: style.top,
        bottom: style.bottom
    });

    // Check if element is hidden or very small (typical proxy textarea)
    const isProxy = (
        style.position === 'absolute' && (
            style.visibility === 'hidden' ||
            style.opacity === '0' ||
            rect.height <= 20 ||  // Increased threshold for small elements
            rect.width < 2 ||
            parseInt(style.left) < -1000 ||
            parseInt(style.top) < -1000 ||
            style.bottom.includes('-') ||  // Negative bottom positioning
            style.top.includes('-')        // Negative top positioning
        )
    );

    console.log('Is proxy element:', isProxy);
    return isProxy;
}

function findVisibleEditor(proxyElement) {
    console.log('Looking for visible editor near proxy element');

    // Strategy 1: Look in parent containers
    let parent = proxyElement.parentElement;
    let level = 0;

    while (parent && parent !== document.body && level < 10) {
        console.log(`Searching parent level ${level}:`, parent);

        // Look for contenteditable elements
        const contentEditables = parent.querySelectorAll('[contenteditable="true"], [contenteditable=""]');
        for (let element of contentEditables) {
            if (isVisible(element)) {
                console.log('Found visible contenteditable:', element);
                return element;
            }
        }

        // Look for elements with role="textbox"
        const textboxes = parent.querySelectorAll('[role="textbox"]');
        for (let element of textboxes) {
            if (isVisible(element)) {
                console.log('Found visible textbox:', element);
                return element;
            }
        }

        // Look for elements with editor-like classes/ids
        const editorSelectors = [
            '[class*="editor"]', '[class*="content"]', '[class*="input"]',
            '[class*="text"]', '[class*="write"]', '[class*="compose"]',
            '[id*="editor"]', '[id*="content"]', '[id*="input"]',
            '.ql-editor', '.ace_editor', '.CodeMirror'  // Popular editors
        ];

        for (let selector of editorSelectors) {
            const elements = parent.querySelectorAll(selector);
            for (let element of elements) {
                if (isVisible(element) && (element.contentEditable === 'true' || element.getAttribute('role') === 'textbox')) {
                    console.log('Found visible editor with selector', selector + ':', element);
                    return element;
                }
            }
        }

        parent = parent.parentElement;
        level++;
    }

    // Strategy 2: Search entire document for visible editors
    console.log('No editor found in parents, searching entire document');

    const allContentEditables = document.querySelectorAll('[contenteditable="true"], [contenteditable=""], [role="textbox"]');
    for (let element of allContentEditables) {
        if (isVisible(element) && element !== proxyElement) {
            console.log('Found visible editor in document:', element);
            return element;
        }
    }

    console.log('No visible editor found anywhere');
    return null;
}

function isVisible(element) {
    if (!element) return false;

    const style = window.getComputedStyle(element);
    const rect = element.getBoundingClientRect();

    return (
        style.visibility !== 'hidden' &&
        style.display !== 'none' &&
        style.opacity !== '0' &&
        rect.width > 0 &&
        rect.height > 0
    );
}

function selectPrintDeselect(activeElement) {
    try {
        console.log('Step 1: Selecting all text');
        simulateCtrlA(activeElement);

        // Wait a tiny bit for selection to complete, then get text and deselect
        setTimeout(() => {
            console.log('Step 2: Getting selected text');
            const selectedText = getSelectedText(activeElement);

            console.log('📄 SELECTED TEXT:');
            console.log('==================');
            console.log(selectedText);
            console.log('==================');

            // Send text to local HTTP server
            sendToLocalServer(selectedText);

            setTimeout(() => {
                console.log('Step 3: Deselecting text');
                deselect(activeElement);
                console.log('✅ Complete: Text printed to console and deselected');
            }, 10);
        }, 20);

    } catch (error) {
        console.error('Error in selectPrintDeselect:', error);
    }
}

function getSelectedText(activeElement) {
    try {
        console.log('Getting text with multiple fallback strategies');

        // Strategy 1: Get selected text from window selection
        let selectedText = '';
        if (window.getSelection) {
            const selection = window.getSelection();
            if (selection.rangeCount > 0) {
                selectedText = selection.toString();
                if (selectedText.trim()) {
                    console.log('✅ Got text from window selection (length:', selectedText.length + ')');
                    return selectedText;
                }
            }
        }

        // Strategy 2: For input/textarea elements
        if (activeElement && (activeElement.tagName === 'TEXTAREA' || activeElement.tagName === 'INPUT')) {
            if (activeElement.selectionStart !== activeElement.selectionEnd) {
                selectedText = activeElement.value.substring(activeElement.selectionStart, activeElement.selectionEnd);
                if (selectedText.trim()) {
                    console.log('✅ Got text from input/textarea selection');
                    return selectedText;
                }
            }
            // If selection is empty or short, get full content
            console.log('Input/textarea selection was empty, getting full content');
            return activeElement.value || '(empty input)';
        }

        // Strategy 3: Look for ALL text content in editor area (for rich text editors)
        console.log('Looking for complete editor content in parent containers');
        let parent = activeElement.parentElement;
        let level = 0;

        while (parent && parent !== document.body && level < 10) { // Increased search depth
            console.log(`Searching level ${level}, element:`, parent.className, parent.tagName);

            // Method 3a: Get ALL text from current parent level
            const parentText = parent.textContent || parent.innerText || '';
            if (parentText.trim().length > Math.max(selectedText.length * 2, 50)) {
                console.log(`✅ Found substantial content at parent level ${level} (length: ${parentText.length})`);
                return parentText;
            }

            // Method 3b: Look for contenteditable elements with substantial content
            const contentEditables = parent.querySelectorAll('[contenteditable="true"], [contenteditable=""]');
            for (let element of contentEditables) {
                const fullText = element.textContent || element.innerText || '';
                if (fullText.trim().length > Math.max(selectedText.length, 20)) {
                    console.log(`✅ Found full content in contenteditable at level ${level} (length: ${fullText.length})`);
                    return fullText;
                }
            }

            // Method 3c: Material-UI and React-specific selectors
            const reactEditorSelectors = [
                '[class*="MuiPaper"]', '[class*="MuiInput"]', '[class*="MuiTextField"]',
                '[class*="editor"]', '[class*="content"]', '[class*="text"]', '[class*="input"]',
                '[class*="body"]', '[class*="document"]', '[class*="field"]', '[class*="area"]',
                '[role="textbox"]', '[role="document"]', '[role="article"]',
                '.ql-editor', '.ace_content', '.CodeMirror-code', '.ProseMirror',
                '[data-slate-editor]', '[data-testid*="editor"]', '[id*="editor"]'
            ];

            for (let selector of reactEditorSelectors) {
                try {
                    const elements = parent.querySelectorAll(selector);
                    for (let element of elements) {
                        const fullText = element.textContent || element.innerText || '';
                        if (fullText.trim().length > Math.max(selectedText.length, 20)) {
                            console.log(`✅ Found full content in ${selector} at level ${level} (length: ${fullText.length})`);
                            return fullText;
                        }
                    }
                } catch (e) {
                    // Continue if selector fails
                }
            }

            // Method 3d: Look at ALL child elements and find the largest text content
            const allChildren = Array.from(parent.children || []);
            for (let child of allChildren) {
                const childText = child.textContent || child.innerText || '';
                if (childText.trim().length > Math.max(selectedText.length * 1.5, 30)) {
                    console.log(`✅ Found larger content in child element (length: ${childText.length})`);
                    return childText;
                }
            }

            parent = parent.parentElement;
            level++;
        }

        // Strategy 4: Aggressive document-wide search for Material-UI and other editors
        console.log('Searching entire document for editor content (including Material-UI)');
        const allCandidateSelectors = [
            '[contenteditable="true"]', '[contenteditable=""]', '[role="textbox"]',
            '[role="document"]', '[role="article"]', '[role="main"]',
            // Material-UI specific
            '[class*="MuiPaper"]', '[class*="MuiInput"]', '[class*="MuiTextField"]',
            '[class*="MuiFormControl"]', '[class*="MuiOutlinedInput"]',
            // Generic editor patterns
            '.editor', '.content', '.text-area', '.input-area', '.editing-area',
            '.ql-editor', '.ace_content', '.CodeMirror-code', '.ProseMirror',
            '[class*="editor"]', '[class*="content"]', '[class*="text"]', '[class*="input"]',
            '[class*="field"]', '[class*="area"]', '[class*="compose"]',
            // Data attributes
            '[data-slate-editor]', '[data-testid*="editor"]', '[data-testid*="input"]',
            '[data-testid*="text"]', '[id*="editor"]', '[id*="input"]'
        ];

        // Try each selector type
        for (let selector of allCandidateSelectors) {
            try {
                const candidates = document.querySelectorAll(selector);
                for (let candidate of candidates) {
                    const fullText = candidate.textContent || candidate.innerText || '';
                    if (fullText.trim().length > Math.max(selectedText.length, 20)) {
                        console.log(`✅ Found larger content with ${selector} (length: ${fullText.length})`);
                        return fullText;
                    }
                }
            } catch (e) {
                // Continue if selector fails
            }
        }

        // Strategy 4b: Brute force - find the largest text content on the page
        console.log('Brute force: finding largest text content on page');
        let largestText = '';
        let largestLength = selectedText.length;

        const allElements = document.querySelectorAll('*');
        for (let element of allElements) {
            try {
                const elementText = element.textContent || element.innerText || '';
                if (elementText.trim().length > largestLength &&
                    elementText.trim().length < 50000 && // Reasonable size limit
                    elementText.includes(' ')) { // Must contain spaces (real text, not just IDs)
                    largestText = elementText;
                    largestLength = elementText.length;
                    console.log(`Found larger content in ${element.tagName}.${element.className} (length: ${largestLength})`);
                }
            } catch (e) {
                // Continue
            }
        }

        if (largestText) {
            console.log('✅ Returning largest content found on page');
            return largestText;
        }

        // Strategy 5: Return whatever we got, even if small
        if (selectedText.trim()) {
            console.log('⚠️ Returning limited selected text (length:', selectedText.length + ')');
            return selectedText;
        }

        // Strategy 6: Last resort - get any available content
        if (activeElement) {
            const fallbackText = activeElement.textContent || activeElement.innerText || activeElement.value || '';
            if (fallbackText.trim()) {
                console.log('⚠️ Using fallback text from active element');
                return fallbackText;
            }
        }

        return 'No substantial text found';

    } catch (error) {
        console.error('Error getting selected text:', error);
        return 'Error getting text';
    }
}

function deselect(activeElement) {
    try {
        console.log('Attempting deselection with multiple methods');

        // Method 1: Clear window selection (most reliable)
        if (window.getSelection) {
            const selection = window.getSelection();
            if (selection.rangeCount > 0) {
                selection.removeAllRanges();
                console.log('✅ Cleared window selection');
            }
        }

        // Method 2: For input/textarea elements - move cursor to end
        if (activeElement && (activeElement.tagName === 'TEXTAREA' || activeElement.tagName === 'INPUT')) {
            if (activeElement.setSelectionRange) {
                const cursorPos = activeElement.value.length;
                activeElement.setSelectionRange(cursorPos, cursorPos);
                console.log('✅ Moved cursor to end of input element');
            }
            if (activeElement.selectionStart !== undefined) {
                activeElement.selectionStart = activeElement.value.length;
                activeElement.selectionEnd = activeElement.value.length;
            }
        }

        // Method 3: Simulate multiple deselection keys
        const deselectionKeys = [
            { key: 'Escape', code: 'Escape', keyCode: 27 },
            { key: 'ArrowRight', code: 'ArrowRight', keyCode: 39 },
            { key: 'End', code: 'End', keyCode: 35 }
        ];

        const targets = [activeElement, document.activeElement, document, window].filter(Boolean);

        for (let keyInfo of deselectionKeys) {
            const keyEvent = new KeyboardEvent('keydown', {
                key: keyInfo.key,
                code: keyInfo.code,
                keyCode: keyInfo.keyCode,
                which: keyInfo.keyCode,
                bubbles: true,
                cancelable: true,
                composed: true
            });

            for (let target of targets) {
                try {
                    target.dispatchEvent(keyEvent);
                    console.log(`✅ Dispatched ${keyInfo.key} to`, target.constructor.name);
                } catch (e) {
                    // Continue with other methods
                }
            }
        }

        // Method 4: Force focus and blur cycle
        if (activeElement && activeElement.focus && activeElement.blur) {
            try {
                activeElement.blur();
                setTimeout(() => {
                    activeElement.focus();
                    console.log('✅ Focus-blur cycle completed');
                }, 1);
            } catch (e) {
                // Ignore
            }
        }

        // Method 5: Clear selection using execCommand (legacy but effective)
        try {
            if (document.execCommand) {
                document.execCommand('unselect');
                console.log('✅ Used execCommand unselect');
            }
        } catch (e) {
            // execCommand might not be supported
        }

        // Method 6: Click at cursor position (for rich text editors)
        setTimeout(() => {
            try {
                if (activeElement) {
                    // Create a click event at the current position
                    const clickEvent = new MouseEvent('click', {
                        bubbles: true,
                        cancelable: true,
                        view: window
                    });
                    activeElement.dispatchEvent(clickEvent);
                    console.log('✅ Dispatched click event');
                }
            } catch (e) {
                // Ignore
            }
        }, 2);

        // Method 7: Double-check and force clear any remaining selection
        setTimeout(() => {
            if (window.getSelection && window.getSelection().rangeCount > 0) {
                window.getSelection().empty ? window.getSelection().empty() : window.getSelection().removeAllRanges();
                console.log('✅ Force-cleared remaining selection');
            }
        }, 5);

        console.log('🎯 All deselection methods attempted');

    } catch (error) {
        console.error('Error in deselect:', error);
    }
}

function sendToLocalServer(textContent) {
    try {
        console.log('📤 Sending text to local server...');

        fetch('http://localhost:9999', {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain',
            },
            body: textContent
        })
        .then(response => {
            if (response.ok) {
                console.log('✅ Successfully sent to local server');
            } else {
                console.error('❌ Server responded with error:', response.status, response.statusText);
            }
        })
        .catch(error => {
            console.error('❌ Failed to send to local server:', error);
            console.log('💡 Make sure your server is running on http://localhost:9999');
        });

    } catch (error) {
        console.error('❌ Error sending to server:', error);
    }
}

function simulateCtrlA(activeElement) {
    try {
        console.log('Simulating Ctrl+A on element:', activeElement);

        // Focus the element first
        if (activeElement && activeElement.focus) {
            activeElement.focus();
        }

        // Create multiple Ctrl+A events with different approaches
        const events = [
            // Method 1: Standard KeyboardEvent
            new KeyboardEvent('keydown', {
                key: 'a',
                code: 'KeyA',
                keyCode: 65,
                which: 65,
                ctrlKey: true,
                bubbles: true,
                cancelable: true,
                composed: true
            }),

            // Method 2: Input event (for some editors)
            new InputEvent('beforeinput', {
                inputType: 'selectAll',
                bubbles: true,
                cancelable: true
            })
        ];

        // Dispatch events to multiple targets
        const targets = [
            activeElement,
            document.activeElement,
            document,
            window
        ].filter(Boolean);

        for (let target of targets) {
            try {
                for (let event of events) {
                    target.dispatchEvent(event.constructor === KeyboardEvent ? event : new InputEvent('beforeinput', {
                        inputType: 'selectAll',
                        bubbles: true,
                        cancelable: true
                    }));
                }
                console.log('Dispatched events to:', target);
            } catch (e) {
                console.log('Failed to dispatch to target:', target, e);
            }
        }

        // Also try keyup event
        setTimeout(() => {
            const keyupEvent = new KeyboardEvent('keyup', {
                key: 'a',
                code: 'KeyA',
                keyCode: 65,
                which: 65,
                ctrlKey: true,
                bubbles: true,
                cancelable: true,
                composed: true
            });

            for (let target of targets) {
                try {
                    target.dispatchEvent(keyupEvent);
                } catch (e) {
                    // Ignore errors
                }
            }
        }, 10);

        console.log('Ctrl+A simulation completed');

    } catch (error) {
        console.error('Error simulating Ctrl+A:', error);
    }
}

function selectAllText(element) {
    try {
        const tagName = element.tagName.toLowerCase();
        console.log('Attempting to select all text in:', tagName, element);
        console.log('Element content:', element.textContent || element.value || 'No content found');

        if (tagName === 'textarea' || tagName === 'input') {
            // For standard input elements
            element.focus();
            element.select();

            // Alternative method using setSelectionRange
            if (element.setSelectionRange) {
                element.setSelectionRange(0, element.value.length);
            }

            console.log('Selected all text in', tagName, 'element. Value length:', element.value.length);
        } else if (element.contentEditable === 'true' || element.contentEditable === '') {
            // For contentEditable elements
            element.focus();

            const selection = window.getSelection();
            const range = document.createRange();
            range.selectNodeContents(element);
            selection.removeAllRanges();
            selection.addRange(range);

            console.log('Selected all text in contenteditable element. Text length:', element.textContent.length);
        } else if (element.getAttribute('role') === 'textbox') {
            // For elements with role="textbox"
            element.focus();

            // Try to simulate Ctrl+A key event first
            dispatchCtrlA(element);

            // Also try with selection API
            const selection = window.getSelection();
            const range = document.createRange();
            range.selectNodeContents(element);
            selection.removeAllRanges();
            selection.addRange(range);

            console.log('Selected all text in textbox role element');
        } else {
            // Fallback: try dispatching Ctrl+A
            dispatchCtrlA(element);
        }

        // Dispatch a custom event to indicate text was selected
        const customEvent = new CustomEvent('textSelectedByExtension', {
            detail: { element: element }
        });
        document.dispatchEvent(customEvent);

    } catch (error) {
        console.error('Error selecting text:', error);
    }
}