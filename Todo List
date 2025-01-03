class Task:
    def __init__(self, description, priority):
        self.description = description
        self.priority = priority

    def __str__(self):
        return f"[{self.priority}] {self.description}"


class ToDoList:
    def __init__(self):
        self.tasks = []

    def add_task(self, description, priority):
        self.tasks.append(Task(description, priority))
        print("Task added successfully!")

    def view_tasks(self):
        if not self.tasks:
            print("No tasks in your To-Do list.")
            return
        print("\nTo-Do List:")
        sorted_tasks = sorted(self.tasks, key=lambda t: t.priority)
        for idx, task in enumerate(sorted_tasks, 1):
            print(f"{idx}. {task}")

    def update_task(self, task_number, description=None, priority=None):
        if 0 < task_number <= len(self.tasks):
            if description:
                self.tasks[task_number - 1].description = description
            if priority:
                self.tasks[task_number - 1].priority = priority
            print("Task updated successfully!")
        else:
            print("Invalid task number.")

    def delete_task(self, task_number):
        if 0 < task_number <= len(self.tasks):
            self.tasks.pop(task_number - 1)
            print("Task deleted successfully!")
        else:
            print("Invalid task number.")


def main():
    todo_list = ToDoList()

    while True:
        print("\n--- To-Do List Menu ---")
        print("1. Add Task")
        print("2. View Tasks")
        print("3. Update Task")
        print("4. Delete Task")
        print("5. Exit")

        choice = input("Enter your choice: ")

        if choice == "1":
            description = input("Enter task description: ")
            priority = input("Enter priority (High, Medium, Low): ").capitalize()
            if priority in ["High", "Medium", "Low"]:
                todo_list.add_task(description, priority)
            else:
                print("Invalid priority. Please enter High, Medium, or Low.")
        elif choice == "2":
            todo_list.view_tasks()
        elif choice == "3":
            todo_list.view_tasks()
            try:
                task_number = int(input("Enter the task number to update: "))
                new_description = input("Enter new description (or press Enter to keep the same): ")
                new_priority = input("Enter new priority (High, Medium, Low) or press Enter to keep the same: ").capitalize()
                todo_list.update_task(
                    task_number,
                    description=new_description if new_description else None,
                    priority=new_priority if new_priority in ["High", "Medium", "Low"] else None,
                )
            except ValueError:
                print("Invalid input. Please enter a number for the task.")
        elif choice == "4":
            todo_list.view_tasks()
            try:
                task_number = int(input("Enter the task number to delete: "))
                todo_list.delete_task(task_number)
            except ValueError:
                print("Invalid input. Please enter a number for the task.")
        elif choice == "5":
            print("Exiting To-Do List. Goodbye!")
            break
        else:
            print("Invalid choice. Please enter a number between 1 and 5.")


if __name__ == "__main__":
    main()
