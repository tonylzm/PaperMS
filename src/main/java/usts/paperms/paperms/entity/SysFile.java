package usts.paperms.paperms.entity;

import jakarta.persistence.*;

@Entity
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

    @Column(name = "is_delete")
    private boolean isDelete;

    @Column(name = "enable")
    private boolean enable;

    // Constructors, getters and setters
    public SysFile() {
    }

    public SysFile(String name, String type, Long size, String url, String md5, boolean isDelete, boolean enable) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.url = url;
        this.md5 = md5;
        this.isDelete = isDelete;
        this.enable = enable;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Long getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

    public String getMd5() {
        return md5;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}

