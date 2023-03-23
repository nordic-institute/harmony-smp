package eu.europa.ec.edelivery.smp.data.model;


public class DBResourceDefDeleteValidation {

    Long id;
    String name;
    Integer count;

    public DBResourceDefDeleteValidation() {
    }

    public DBResourceDefDeleteValidation(Long id, String name, Integer count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceDefName() {
        return name;
    }

    public void setResourceDefName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
