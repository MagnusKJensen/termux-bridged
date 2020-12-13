package dk.aau.sw711e20;

public class ProcessingManager implements Runnable{

    private boolean activated = false;

    public void deactivate() {
        activated = false;
    }

    public void activate() {
        activated = true;
    }

    @Override
    public void run() {
        while (true) {
            if (activated) {

            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
