import tkinter as tk
from tkinter import ttk, scrolledtext, filedialog, messagebox
import threading
import os
from datetime import datetime
from PIL import Image, ImageDraw
import pystray
from functools import partial
import time
from OpenAiAsk import ask_openai_question
from http.server import BaseHTTPRequestHandler, HTTPServer
import json

class FileMonitorApp:
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("File Monitor")
        self.root.geometry("800x600")
        
        # Variables
        self.polling_active = False
        self.polling_thread = None
        self.monitor_path = "C:\\mygit\\TEST"
        self.command_file_path = os.path.join(self.monitor_path, "currFile.txt")
        self.tray_icon = None
        
        # Setup UI
        self.setup_ui()
        
        # Setup system tray
        self.setup_tray()

        # Set as a tool window to hide from taskbar
        self.root.attributes('-toolwindow', 1)

        # Maximize window on startup
        self.root.state('zoomed')

        # Show the window on startup
        self._show_window()

        # Start the polling loop
        self.start_polling()
        
        # Start the HTTP server
        self.start_http_server()

        # Handle window close button
        self.root.protocol('WM_DELETE_WINDOW', self.minimize_to_tray)
    
    def create_icon(self):
        # Create a simple icon
        width = 16
        height = 16
        image = Image.new('RGB', (width, height), (255, 255, 255))
        dc = ImageDraw.Draw(image)
        dc.rectangle([4, 4, 12, 12], fill=(0, 122, 204))
        return image
    
    def setup_tray(self):
        # Create menu items
        menu = (
            pystray.MenuItem('Show', self.show_window),
            pystray.MenuItem('Exit', self.quit_application)
        )
        
        # Create the system tray icon
        self.tray_icon = pystray.Icon(
            "file_monitor",
            icon=self.create_icon(),
            menu=pystray.Menu(*menu)
        )
        
        # Start the system tray in a separate thread
        self.tray_thread = threading.Thread(target=self.tray_icon.run, daemon=True)
        self.tray_thread.start()
    
    def show_window(self, icon=None, item=None):
        self.root.after(0, self._show_window)
    
    def _show_window(self):
        self.root.deiconify()
        self.root.lift()
        self.root.focus_force()
        # Make sure window appears in taskbar when shown
        self.root.attributes('-topmost', True)
        self.root.after(100, lambda: self.root.attributes('-topmost', False))
    
    def minimize_to_tray(self):
        self.root.withdraw()
        # Hide from taskbar when minimized to tray
        self.root.attributes('-topmost', False)
    
    def quit_application(self, icon=None, item=None):
        # Stop the polling thread
        self.stop_polling()
        
        # Remove the tray icon
        if hasattr(self, 'tray_icon') and self.tray_icon:
            self.tray_icon.stop()
        
        # Destroy the root window
        self.root.quit()
        self.root.destroy()
    
    def setup_ui(self):
        # Top frame for controls
        top_frame = ttk.Frame(self.root)
        top_frame.pack(fill=tk.X, padx=5, pady=5)
        
        
        
        # Clear button
        clear_btn = ttk.Button(top_frame, text="Clear", command=self.clear_log)
        clear_btn.pack(side=tk.LEFT, padx=(0, 5))
        
        # Status label
        self.status_label = ttk.Label(top_frame, text="Status: Polling every 2s")
        self.status_label.pack(side=tk.LEFT, padx=(10, 0))
        
        self.path_label = ttk.Label(top_frame, text=f"File: {self.command_file_path}", foreground="gray")
        self.path_label.pack(side=tk.RIGHT)
        
        # Text area for logs
        self.text_area = scrolledtext.ScrolledText(
            self.root,
            font=("Consolas", 8),
            wrap=tk.WORD,
            state=tk.DISABLED
        )
        self.text_area.pack(fill=tk.BOTH, expand=True, padx=5, pady=(0, 5))
        
        # Initial message
        self.add_log(f"File Monitor started. Polling for changes in: {self.command_file_path}")
    
    def update_status(self, message):
        self.status_label.config(text=f"Status: {message}")
    
    
    def poll_file_for_changes(self):
        # Create directory and file if they don't exist on first run
        self.setup_monitoring_target()

        while self.polling_active:
            try:
                with open(self.command_file_path, 'r') as f:
                    content = f.read().strip()
                
                if content.startswith('DID'):
                    self.update_status('File has not changed. Waiting...')
                else:
                    self.update_status('File has changed. Processing...')
                    # Since the file doesn't start with DID, process it.
                    self.process_command_file(self.command_file_path)

            except FileNotFoundError:
                self.update_status(f"Command file not found. Re-creating.")
                self.setup_monitoring_target()
            except Exception as e:
                self.update_status(f"Error during polling: {e}")

            time.sleep(2)

    def process_command_file(self, file_path):
        try:
            with open(file_path, 'r+') as f:
                content = f.read().strip()

                if content.startswith('DO,'):
                    self.add_log(f'Detected DO command in {os.path.basename(file_path)}.')
                    parts = content.split(',', 1)
                    if len(parts) == 2:
                        target_filepath = parts[1].strip()
                        self.add_log(f'Processing DO command for: {target_filepath}')

                        if os.path.exists(target_filepath):
                            with open(target_filepath, 'r', encoding='utf-8') as target_file:
                                search_query = target_file.read().strip()
                            
                            if search_query:
                                self.add_log(f'Calling OpenAI with content from: "{target_filepath}"')
                                try:
                                    # Perform the OpenAI call
                                    search_results = ask_openai_question(search_query)
                                    self.clear_log()
                                    self.add_log(f'--- OpenAI Response ---\n{search_results}\n----------------------')
                                except Exception as service_error:
                                    self.add_log(f'Error calling OpenAI service: {service_error}')

                            # Update the command file by replacing DO with DID
                            new_content = content.replace('DO,', 'DID,', 1)
                            f.seek(0)
                            f.truncate()
                            f.write(new_content)
                            self.add_log(f'Updated {os.path.basename(file_path)} to: {new_content}')
                        else:
                            self.add_log(f'Error: File not found at {target_filepath}')
                            f.seek(0)
                            f.truncate()
                            f.write(f'ERROR,File not found: {target_filepath}')
        except Exception as e:
            self.add_log(f'Error processing command file: {str(e)}')


    def setup_monitoring_target(self):
        try:
            if not os.path.exists(self.monitor_path):
                os.makedirs(self.monitor_path)
                self.add_log(f'Created directory: {self.monitor_path}')
            if not os.path.exists(self.command_file_path):
                with open(self.command_file_path, 'w') as f:
                    f.write('Initial state')
                self.add_log(f'Created command file: {self.command_file_path}')
        except Exception as e:
            self.add_log(f'Error setting up monitoring target: {e}')

    def start_polling(self):
        if not self.polling_active:
            self.polling_active = True
            self.polling_thread = threading.Thread(target=self.poll_file_for_changes, daemon=True)
            self.polling_thread.start()
            self.add_log("Started polling thread.")

    def start_http_server(self):
        """Start the HTTP server in a separate thread."""
        def run_server():
            server_address = ('', 9999)
            httpd = HTTPServer(server_address, HTTPRequestHandler)
            httpd.app = self  # Pass app reference to handler
            self.add_log(f"HTTP server started on port 9999")
            httpd.serve_forever()
            
        self.http_thread = threading.Thread(target=run_server, daemon=True)
        self.http_thread.start()

    def stop_polling(self):
        if self.polling_active:
            self.polling_active = False
            if self.polling_thread and self.polling_thread.is_alive():
                self.polling_thread.join(timeout=1) # Wait for thread to finish
            self.add_log("Stopped polling thread.")
    
    def add_log(self, message):
        self.text_area.config(state=tk.NORMAL)
        self.text_area.insert(tk.END, message + "\n")
        self.text_area.see(tk.END)
        self.text_area.config(state=tk.DISABLED)
    
    def clear_log(self):
        self.text_area.config(state=tk.NORMAL)
        self.text_area.delete(1.0, tk.END)
        self.text_area.config(state=tk.DISABLED)

def main():
    app = FileMonitorApp()
    app.root.mainloop()

class HTTPRequestHandler(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.send_header('Content-type', 'application/json')
    
    def do_OPTIONS(self):
        self.send_response(200)
        self._set_headers()
        self.end_headers()
    
    def do_POST(self):
        content_length = int(self.headers.get('Content-Length', 0))
        if content_length == 0:
            self.send_response(400)
            self._set_headers()
            self.end_headers()
            self.wfile.write(json.dumps({"status": "error", "message": "Content-Length header is required"}).encode())
            return
            
        post_data = self.rfile.read(content_length)
        
        try:
            # Get the app instance from the server
            app = self.server.app
            
            # Ensure the directory exists
            os.makedirs('c:\\tmp', exist_ok=True)
            
            # Write the request body to the file
            with open('c:\\tmp\\question.txt', 'wb') as f:
                f.write(post_data)
            
            # Update C:\\mygit\\TEST\\currFile.txt by replacing 'DID' with 'DO'
            try:
                curr_file_path = 'C:\\mygit\\TEST\\currFile.txt'
                if os.path.exists(curr_file_path):
                    with open(curr_file_path, 'r+') as f:
                        content = f.read()
                        if 'DID' in content:
                            f.seek(0)
                            f.write(content.replace('DID', 'DO', 1))
                            f.truncate()
                            app.add_log(f"Updated {curr_file_path} - replaced 'DID' with 'DO'")
                else:
                    app.add_log(f"Warning: {curr_file_path} not found")
            except Exception as e:
                app.add_log(f"Error updating {curr_file_path}: {e}")
            
            app.add_log(f"Received and saved request to c:\\tmp\\question.txt")
            
            # Send response
            self.send_response(200)
            self._set_headers()
            self.end_headers()
            self.wfile.write(json.dumps({"status": "success"}).encode())
            
        except Exception as e:
            self.send_response(500)
            self._set_headers()
            self.end_headers()
            self.wfile.write(json.dumps({"status": "error", "message": str(e)}).encode())

if __name__ == "__main__":
    main()