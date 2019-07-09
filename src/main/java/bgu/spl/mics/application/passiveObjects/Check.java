package bgu.spl.mics.application.passiveObjects;

public class Check {
    public Integer logisticTerm;
    public Integer totalLogistic;

    private static class Holder {
        private static final Check INSTANCE = new Check();
    }


    private Check() {
    }

    public static Check getInstance() {

        return Check.Holder.INSTANCE;
    }


}
