package interfaces;

import java.io.IOException;

/**
 * Common persistence contract for saving and loading system state.
 *
 * <p>Implementing classes are responsible for serializing the entire
 * application dataset to persistent storage and restoring it on demand.</p>
 */
public interface Persistable {

    /**
     * Persists the current system state to storage.
     *
     * @throws IOException if an I/O error occurs during the save operation
     */
    void save() throws IOException;

    /**
     * Restores system state from persistent storage.
     *
     * @throws IOException if an I/O error occurs during the load operation
     */
    void load() throws IOException;
}
