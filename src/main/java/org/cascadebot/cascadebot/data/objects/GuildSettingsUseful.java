package org.cascadebot.cascadebot.data.objects;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuildSettingsUseful {

    public GuildSettingsUseful() {

    }

    private Map<String, TodoList> todoLists = new ConcurrentHashMap<>();

    //region todo list stuff
    public TodoList getTodoList(String name) {
        return todoLists.get(name);
    }

    public TodoList createTodoList(String name, long owner) {
        if (todoLists.containsKey(name)) {
            return null;
        }
        TodoList todoList = new TodoList(owner);
        todoLists.put(name, todoList);
        return todoList;
    }

    public void deleteTodoList(String name) {
        todoLists.remove(name);
    }
    //endregion

}
