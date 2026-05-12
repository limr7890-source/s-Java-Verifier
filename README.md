# 🚀 s-Java Static Code Verifier

## What is this project?
Ever wondered how a compiler knows you forgot a semicolon or used a variable that doesn't exist? This project is a **Static Code Verifier** built in Java that does exactly that for a simplified version of the Java language.

It’s a tool that "reads" code, analyzes its structure, and decides if it’s legally written—all without ever actually running the program.

---

## 🧠 The Challenge
Writing a tool that understands code is tricky. I had to handle:

* **Nested Scopes:** Making sure a variable defined inside an `if` block doesn't "leak" outside—just like in real Java.
* **Pattern Recognition:** Using complex **Regular Expressions (Regex)** to identify everything from variable declarations to method signatures.
* **Logic Validation:** Ensuring that types match and that methods are called correctly.

---

## 🛠️ How I Built It
Instead of just writing one long script, I used a modular approach with **Design Patterns** (like *Facade* and *Factory*) to keep the code clean and organized. This made it much easier to manage the different "rules" of the s-Java language.

## 💻 Tech Stack
* **Language:** `Java`
* **Core Concepts:** `Regex`, `Object-Oriented Design`, `Scope Management`
