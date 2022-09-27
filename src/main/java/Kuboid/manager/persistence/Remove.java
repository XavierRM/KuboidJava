package Kuboid.manager.persistence;

import org.joml.Vector3f;

public class Remove extends Change {

    public Remove(Vector3f location) {
        setLocation(location);
    }
}
