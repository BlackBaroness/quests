package quest.quest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Save extends Thread {

    private final Manager manager;

    Save(Manager manager) {
        this.manager = manager;
    }

    @Override
    public synchronized void start() {
        try {
            File file = new File(manager.getCore().getDataFolder() + File.separator + "quests");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(manager.getData());
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
