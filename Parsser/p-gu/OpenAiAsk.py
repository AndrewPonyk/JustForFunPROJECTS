import os
from openai import OpenAI

def ask_openai_question(question):
    try:
        client = OpenAI() # The API key is loaded automatically from the OPENAI_API_KEY environment variable.

        print("Sending question to OpenAI...")

        response = client.chat.completions.create(
            model="gpt-5-mini",
            messages=[{
                    "role": "system",
                    "content": "You are super architect with deep knowledge of programming and IT. Provide concise, accurate, and expert-level answers. If content of questions is sorce code and you see "
                    + "some unimplemented functions or some unimplemented  comments implement that comments. In case of general code tasks use  JAVA LANG. IN case of programming questions give short anwers."
                },
                {
                    "role": "user",
                    "content": question
                }])

        answer = response.choices[0].message.content
        print("\n--- OpenAI Super Architect Says ---")
        print(answer)
        print("-------------------------------------")

        usage = response.usage
        print("\n--- Token Usage ---")
        print(f"Prompt tokens: {usage.prompt_tokens}")
        print(f"Completion tokens: {usage.completion_tokens}")
        print(f"Total tokens: {usage.total_tokens}")
        print("-------------------")
        return answer
    except Exception as e:
        print(f"An error occurred: {e}")
        print("Please ensure your OPENAI_API_KEY environment variable is set correctly.")

if __name__ == "__main__":
    ask_openai_question("what is new keywords in java 17")