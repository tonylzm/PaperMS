package usts.paperms.paperms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "url")
    private String url;

    @Column(name = "md5")
    private String md5;

    @Column(name="produced")
    private String produced;

    @Column(name="fromon")
    private String fromon;

    @Column(name = "is_delete")
    private boolean isDelete;

    @Column(name = "enable")
    private boolean enable;

    // Constructors, getters and setters
    public SysFile() {
    }

    public SysFile(String name, String type, Long size, String url, String md5, String produced,String fromon,boolean isDelete, boolean enable) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.url = url;
        this.md5 = md5;
        this.produced= produced;
        this.fromon=fromon;
        this.isDelete = isDelete;
        this.enable = enable;
    }


}

