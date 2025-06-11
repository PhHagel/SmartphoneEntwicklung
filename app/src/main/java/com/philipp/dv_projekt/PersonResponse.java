package com.philipp.dv_projekt;

public class PersonResponse {
    public Person message;

    public static class Person {
        public String firstname;
        public String lastname;
        public String sex;
        public String date_of_birth;
        public String phone_number;
        public String email_address;
    }

}
