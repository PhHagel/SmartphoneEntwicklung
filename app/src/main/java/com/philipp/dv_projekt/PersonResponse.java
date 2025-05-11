package com.philipp.dv_projekt;

public class PersonResponse {
    public Person message;

    public static class Person {
        public String dateOfBirth;
        public String emailAddress;
        public String firstname;
        public String lastname;
        public String phoneNumber;
        public String sex;
    }
}
