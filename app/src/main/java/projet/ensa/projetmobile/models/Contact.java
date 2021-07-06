package projet.ensa.projetmobile.models;

public class Contact {
    private String mName;
    private String mNumber;

    public Contact(String name, String number) {
        mName = name;
        mNumber=number;

    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmNumber() {
        return mNumber;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "mName='" + mName + '\'' +
                ", mNumber='" + mNumber + '\'' +
                '}';
    }
}