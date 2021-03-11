package com.neige_i.go4lunch.data.firebase;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neige_i.go4lunch.LiveDataTestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.neige_i.go4lunch.data.firebase.FirestoreRepositoryImpl.USER_COLLECTION;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class FirestoreRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FirestoreRepository firestoreRepository;

    private final FirebaseFirestore firebaseFirestore = mock(FirebaseFirestore.class);

    private final CollectionReference userCollection = mock(CollectionReference.class);
    private final DocumentReference userDocument = mock(DocumentReference.class);
    private final Task<DocumentSnapshot> documentSnapshotTask = mock(Task.class);

    @Before
    public void setUp() {
        doReturn(userCollection).when(firebaseFirestore).collection(USER_COLLECTION);
        doReturn(userDocument).when(userCollection).document(anyString());
        doReturn(documentSnapshotTask).when(userDocument).get();

        firestoreRepository = new FirestoreRepositoryImpl(firebaseFirestore);
    }

    @Test
    public void getUserTest() throws InterruptedException {
        // Given
        final String uid = "USER_ID";

        // When
        final User userFromFirestore = LiveDataTestUtils.getOrAwaitValue(firestoreRepository.getUser(uid));

        // Then
        assertEquals(
            getDefaultUser(),
            userFromFirestore
        );
    }

    private User getDefaultUser() {
        return new User(
            "USER_ID",
            "myEmail",
            "myName"
        );
    }
}