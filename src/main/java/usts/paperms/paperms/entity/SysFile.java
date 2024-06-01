package usts.paperms.paperms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Getter
@Setter
@Table(name = "sys_file")
public class SysFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "size")
    private Long size;

    @Column(name = "url" , columnDefinition = "TEXT")
    @JsonIgnore
    private String url;

    @Column(name = "md5")
    @JsonIgnore
    private String md5;

    @Column(name="produced")
    private String produced;

    @Column(name="testtime")
    private String testtime;

    @Column(name = "is_decrypt")
    private boolean decrypt;

    @Column(name = "testtype")
    private String testtype;

    @Column(name = "testname")
    private String testname;

    @Column(name ="classes")
    private String classes;

    @Column(name ="college")
    private String college;

    @Column(name = "is_pigeonhole")
    private boolean pigeonhole;

    @Column(name="upload_time", columnDefinition = "TIMESTAMP")
    private String uploadTime;


    @OneToOne(mappedBy = "sysFile",cascade = CascadeType.ALL)

    private Check check;

    // Constructors, getters and setters
    public SysFile() {
    }

    public SysFile(String name, String type, Long size, String url, String md5,
                   String produced,String testtype,String testtime, boolean decrypt,
                   String classes,String college,String testname) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.url = url;
        this.md5 = md5;
        this.produced= produced;
        this.testtime=testtime;
        this.decrypt = decrypt;
        this.testtype=testtype;
        this.classes=classes;
        this.college=college;
        this.testname=testname;
    }


}

