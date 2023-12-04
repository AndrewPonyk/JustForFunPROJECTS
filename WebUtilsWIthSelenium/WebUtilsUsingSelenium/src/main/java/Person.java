public class Person {
    private String name;
    private int age;
    private String address;
    private String phoneNumber;
    private String email;

    public Person(String name, int age, String address, String phoneNumber, String email) {
        this.name = name;
        this.age = age;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public void removePhoneNumber() {
        this.phoneNumber = null;
    }

    public String concatEmailWitnPersonAgeInDays(){
        return this.email + this.age * 365;}
}
