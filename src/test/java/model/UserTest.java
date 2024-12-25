package model;

import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserTest {

    @Test public void createUser(){
        String userId = "chan9301";
        String password = "1234";
        String name = "chan";
        String email = "chan9301@naver.com";


        User user = new User(userId,password,name,email);

        assertThat(user.getUserId(), is(userId));
        assertThat(user.getPassword(), is(password));
        assertThat(user.getEmail(), is(email));
        assertThat(user.getName(), is(name));

    }


}
