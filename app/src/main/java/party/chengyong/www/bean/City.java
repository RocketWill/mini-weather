package party.chengyong.www.bean;

public class City {
    private String privince;
    private String city;
    private String number;
    private String firstPY;
    private String allPY;
    private String allFirstPY;

    public City(String privince, String city, String number, String firstPY, String allPY, String allFirstPY) {
        this.privince = privince;
        this.city = city;
        this.number = number;
        this.firstPY = firstPY;
        this.allPY = allPY;
        this.allFirstPY = allFirstPY;
    }

    public String getPrivince() {
        return privince;
    }

    public String getCity() {
        return city;
    }

    public String getNumber() {
        return number;
    }

    public String getFirstPY() {
        return firstPY;
    }

    public String getAllPY() {
        return allPY;
    }

    public String getAllFirstPY() {
        return allFirstPY;
    }

    public void setPrivince(String privince) {
        this.privince = privince;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setFirstPY(String firstPY) {
        this.firstPY = firstPY;
    }

    public void setAllPY(String allPY) {
        this.allPY = allPY;
    }

    public void setAllFirstPY(String allFirstPY) {
        this.allFirstPY = allFirstPY;
    }

    @Override
    public String toString() {
        return "City{" +
                "privince='" + privince + '\'' +
                ", city='" + city + '\'' +
                ", number='" + number + '\'' +
                ", firstPY='" + firstPY + '\'' +
                ", allPY='" + allPY + '\'' +
                ", allFirstPY='" + allFirstPY + '\'' +
                '}';
    }
}
