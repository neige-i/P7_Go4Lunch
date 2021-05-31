//package com.neige_i.go4lunch.domain;
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
//
//import com.neige_i.go4lunch.data.firebase.FirestoreRepository;
//import com.neige_i.go4lunch.data.firebase.model.User;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//
//public class CreateFirestoreUserUseCaseImplTest {
//
//    @Rule
//    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
//
//    private CreateFirestoreUserUseCase createFirestoreUserUseCase;
//
//    private final FirestoreRepository firestoreRepository = mock(FirestoreRepository.class);
//
//    @Before
//    public void setUp() {
//        createFirestoreUserUseCase = new CreateFirestoreUserUseCaseImpl(firestoreRepository);
//    }
//
//    @Test
//    public void firestoreUser_createNew() {
//        // Given
//        final User userToAdd = new User("myId", "myEmail", "myName");
//
//        // When
//        createFirestoreUserUseCase.createUser("USER_ID", userToAdd);
//
//        // Then
//        verify(firestoreRepository).addUser("USER_ID", userToAdd);
//    }
//}