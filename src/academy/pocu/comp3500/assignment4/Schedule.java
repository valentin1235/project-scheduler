package academy.pocu.comp3500.assignment4;

import academy.pocu.comp3500.assignment4.project.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Schedule {
    public static List<Task> find(final Task[] tasks, final boolean includeMaintenance) {
        List<Task> taskList = new LinkedList<>();
        HashMap<String, Boolean> visited = new HashMap<>();
        HashMap<String, Boolean> processed = new HashMap<>();
        HashMap<String, LinkedList<Task>> outCycles = new HashMap<>();

        for (Task task : tasks) {
            taskList.add(task);
            visited.put(task.getTitle(), false);
            processed.put(task.getTitle(), false);
            outCycles.put(task.getTitle(), null);
        }

        LinkedList<Task> reversed = reverseGraph(taskList);

        LinkedList<Task> preOrdered = preOrder(reversed, visited);

        visited.replaceAll((k, v) -> false);
        LinkedList<LinkedList<Task>> cycles = cycleDetect(preOrdered, visited, processed, outCycles);

        List<Task> ret = new ArrayList<>();
        while (!cycles.isEmpty()) {
            LinkedList<Task> cycle = cycles.removeLast();
            if (!includeMaintenance && cycle.size() > 1) {
                continue;
            }

            while (!cycle.isEmpty()) {
                Task t = cycle.pop();
//                System.out.println(t.getTitle());
                ret.add(t);
            }
//            System.out.println("------------------");
        }

        return ret;
    }

    private static LinkedList<Task> preOrder(List<Task> tasks, HashMap<String, Boolean> visited) {
        LinkedList<Task> sorted = new LinkedList<>();

        for (Task task : tasks) {
            preOrderRecursive(task, visited, sorted);
        }

        return sorted;
    }

    private static void preOrderRecursive(Task task, HashMap<String, Boolean> visited, LinkedList<Task> sorted) {
        if (task == null || visited.get(task.getTitle())) {
            return;
        }

        visited.put(task.getTitle(), true);

        for (Task predecessor : task.getPredecessors()) {
            if (!visited.get(predecessor.getTitle())) {
                preOrderRecursive(predecessor, visited, sorted);
            }
        }

        sorted.addLast(task);
    }

    private static LinkedList<Task> reverseGraph(List<Task> tasks) {
        LinkedList<Task> newTasks = new LinkedList<>();
        HashMap<String, Integer> indexes = new HashMap<>();

        for (int i = 0; i < tasks.size(); ++i) {
            Task task = tasks.get(i);
            newTasks.add(new Task(task.getTitle(), task.getEstimate()));
            indexes.put(newTasks.get(i).getTitle(), i);
        }

        for (Task task : tasks) {
            List<Task> predecessors = task.getPredecessors();
            for (Task predecessor : predecessors) {
                Task newTask = newTasks.get(indexes.get(task.getTitle()));
                Task newPredecessor = newTasks.get(indexes.get(predecessor.getTitle()));
                newPredecessor.addPredecessor(newTask);
            }
        }

        return newTasks;
    }

    private static LinkedList<LinkedList<Task>> cycleDetect(LinkedList<Task> tasks, HashMap<String, Boolean> visited, HashMap<String, Boolean> processed, HashMap<String, LinkedList<Task>> outCycles) {
        LinkedList<LinkedList<Task>> cycles = new LinkedList<>();

        while (!tasks.isEmpty()) {
            Task task = tasks.removeLast();
            cycleDetectRecursive(task, visited, processed, outCycles, cycles);
        }

        return cycles;
    }


    private static void cycleDetectRecursive(Task task, HashMap<String, Boolean> visited, HashMap<String, Boolean> processed, HashMap<String, LinkedList<Task>> outCycles, List<LinkedList<Task>> cycles) {
        if (task == null || visited.get(task.getTitle())) {
            return;
        }

        visited.put(task.getTitle(), true);

        for (Task predecessor : task.getPredecessors()) {
            if (!visited.get(predecessor.getTitle())) {
                cycleDetectRecursive(predecessor, visited, processed, outCycles, cycles);
            } else if (!processed.get(predecessor.getTitle()) && outCycles.get(predecessor.getTitle()) == null) {
                LinkedList<Task> cycle = new LinkedList<>();
                if (outCycles.get(task.getTitle()) != null) {
                    cycle = outCycles.get(task.getTitle());
                }

                dfsCycle(predecessor, predecessor, outCycles, cycle, processed, initVisited(visited.keySet()));
                if (cycles.size() > 0 && isIncluded(cycles.get(cycles.size() - 1), cycle)) {
                    cycles.set(cycles.size() - 1, cycle);
                } else {
                    cycles.add(cycle);
                }
            }
        }

        if (outCycles.get(task.getTitle()) == null) {
            LinkedList<Task> cycle = new LinkedList<>();
            cycle.add(task);
            cycles.add(cycle);
        }
        processed.put(task.getTitle(), true);
    }


    private static void dfsCycle(Task task, Task root, HashMap<String, LinkedList<Task>> outCycles, LinkedList<Task> cycle, HashMap<String, Boolean> processed, HashMap<String, Boolean> visited) {
        visited.put(task.getTitle(), true);

        for (Task predecessor : task.getPredecessors()) {

            if (predecessor.equals(root) || processed.get(predecessor.getTitle())) {
                break;
            }

            if (!visited.get(predecessor.getTitle())) {
                dfsCycle(predecessor, root, outCycles, cycle, processed, visited);
            }
        }

        cycle.push(task);
        outCycles.put(task.getTitle(), cycle);
    }

    private static HashMap<String, Boolean> initVisited(Set<String> keys) {
        HashMap<String, Boolean> visited = new HashMap<>();

        for (String key : keys) {
            visited.put(key, false);
        }

        return visited;
    }

    private static boolean isIncluded(LinkedList<Task> small, LinkedList<Task> big) {
        for (Task task : big) {
            if (task.getTitle().equals(small.get(0).getTitle())) {
                return true;
            }
        }

        return false;
    }
}