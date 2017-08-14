package com.android.btcomm;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by Gowtham on 13-08-2017.
 */

@Table(name = "ChatDB")
public class DBStore1 extends Model {

    @Column(name = "From_Addr") String from;
    @Column(name = "To_Addr") String to;
    @Column(name = "Mode") String mode;
    @Column(name = "Message") String message;
    @Column(name = "Timestamp") String timestamp;

    @Override
    public String toString() {
        return "DBStore1{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", mode='" + mode + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
