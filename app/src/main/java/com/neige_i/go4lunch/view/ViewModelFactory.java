package com.neige_i.go4lunch.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.neige_i.go4lunch.view.home.HomeViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    // -------------------------------------  CLASS VARIABLES --------------------------------------

    @Nullable
    private static ViewModelFactory factory;

    // ----------------------------------- INJECTED DEPENDENCIES -----------------------------------

//    @NonNull
//    private final TaskRepository taskRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

//    private ViewModelFactory(@NonNull TaskRepository taskRepository) {
//        this.taskRepository = taskRepository;
//    }

    // -------------------------------------- FACTORY METHODS --------------------------------------

    @NonNull
    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory(
//                        new TaskRepository(
//                            TaskDatabase.getInstance(MainApplication.getInstance()).taskDao(),
//                            Executors.newSingleThreadExecutor()
//                        )
                    );
                }
            }
        }
        return factory;
    }

    // -------------------------------- VIEW MODEL FACTORY METHODS ---------------------------------

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel();
//            return (T) new ListViewModel(taskRepository);
        }
//        } else if (modelClass.isAssignableFrom(AddTaskViewModel.class)) {
//            return (T) new AddTaskViewModel(taskRepository, Clock.systemDefaultZone());
//        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
