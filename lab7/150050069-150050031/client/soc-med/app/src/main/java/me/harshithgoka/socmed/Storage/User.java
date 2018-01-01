package me.harshithgoka.socmed.Storage;

import java.io.Serializable;

/**
 * Created by harshithgoka on 07/10/17.
 */

public class User implements Serializable {
    public String uid;
    public String name;
    public String email;
    public boolean following;
}
