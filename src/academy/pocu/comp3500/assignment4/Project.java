package academy.pocu.comp3500.assignment4;

import academy.pocu.comp3500.assignment4.project.Task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


public final class Project {
    final Task[] tasks;

    public Project(final Task[] tasks) {
        this.tasks = tasks;
    }

    public int findTotalManMonths(final String taskName) {
        HashMap<String, Boolean> visited = new HashMap<>();
        HashMap<String, Boolean> processed = new HashMap<>();
        HashMap<String, Boolean> cycled = new HashMap<>();
        LinkedList<Task> sorted = new LinkedList<>();
        for (Task task : tasks) {
            visited.put(task.getTitle(), false);
            processed.put(task.getTitle(), false);
            cycled.put(task.getTitle(), false);
        }

        Task task = findTaskOrNull(taskName);

        dfs(task, visited, processed, sorted, cycled);

        int totalManMonth = 0;
        for (Task t : sorted) {
            if (!cycled.get(t.getTitle())) {
                totalManMonth += t.getEstimate();
            }

        }

        return totalManMonth;
    }

    private void dfs(Task task, HashMap<String, Boolean> visited, HashMap<String, Boolean> processed, LinkedList<Task> sorted, HashMap<String, Boolean> cycled) {
        if (task == null || visited.get(task.getTitle())) {
            return;
        }

        visited.put(task.getTitle(), true);

        for (Task predecessor : task.getPredecessors()) {
            if (!visited.get(predecessor.getTitle())) {
                dfs(predecessor, visited, processed, sorted, cycled);
            } else if (!processed.get(predecessor.getTitle())) {
//                System.out.printf("task: %s, predecessor: %s\n", task.getTitle(), predecessor.getTitle());
                excludeCycle(predecessor, predecessor, cycled);
            }
        }

        sorted.add(task);
        processed.put(task.getTitle(), true);
    }

    private void excludeCycle(Task task, Task root, HashMap<String, Boolean> cycled) {

        for (Task predecessor : task.getPredecessors()) {
            if (predecessor.equals(root)) {
                break;
            }

            excludeCycle(predecessor, root,cycled);
        }

        cycled.put(task.getTitle(), true);
    }



    public int findMinDuration(final String taskName) {
        Task task = findTaskOrNull(taskName);
        assert task != null;

        HashMap<String, Integer> distances = new HashMap<>();
        Queue<Task> queue = new LinkedList<>();

        distances.put(task.getTitle(), 0);
        queue.add(task);


        int minDuration = 0;

        int distance;
        int maxEstimate;
        while (!queue.isEmpty()) {
            Task cur = queue.remove();
            distance = distances.get(cur.getTitle());
            Task next = queue.peek();

            if (next != null && distance == distances.get(next.getTitle())) {
                maxEstimate = Math.max(cur.getEstimate(), next.getEstimate());
            } else {
                maxEstimate = cur.getEstimate();
                minDuration += maxEstimate;
//                System.out.println(cur.getTitle());
            }






            for (Task predecessor : cur.getPredecessors()) {
                if (!distances.containsKey(predecessor.getTitle())) {
//                    System.out.println(distance + " " + predecessor.getTitle() + " " + predecessor.getEstimate());
                    queue.add(predecessor);
                    distances.put(predecessor.getTitle(), distance + 1);


                }
            }
        }

        return minDuration;
    }

    public int findMaxBonusCount(final String task) {
        return -1;
    }

    private Task findTaskOrNull(final String taskName) {
        for (Task task : tasks) {
            if (taskName.equals(task.getTitle())) {
                return task;
            }
        }

        return null;
    }
}