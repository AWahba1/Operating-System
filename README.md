# Operating-System

# Description
The project was a simulation of a real operating system, where OS manages resources and processes. 

# Features & Functionalities
- Interpreter that reads a text file representing a program and starts executing it.
- Mutexes to ensure mutual exclusion over the critical resources.
- Scheduler that schedule processes in the system to ensure all processes gets a chance to execute. Scheduling algorthim used was Round Robin. 
- System calls that reads and writes data from disk and memory
- Memory that's shared by all processes to store program code and variables that processes need to complete execution. In case memory doesn't fit, a LIFO (Last In - First Out) replacement algorthim was used.
- A Process Control Block (PCB) for each process to store all information about a process including process ID and state.

