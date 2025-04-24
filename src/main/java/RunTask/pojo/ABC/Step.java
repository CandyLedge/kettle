package RunTask.pojo.ABC;

public  class Step {
    protected Integer step;
    protected String description;
    protected String type;
    protected String privateField;
    
    public String getPrivateField() {
        return privateField;
    }
    
    public void setPrivateField(String privateField) {
        this.privateField = privateField;
    }
    
    public Integer getStep() {
        return step;
    }
    
    public void setStep(Integer step) {
        this.step = step;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Step() {
    }
    
    public Step(Integer step, String description, String type, String privateField) {
        this.step = step;
        this.description = description;
        this.type = type;
        this.privateField = privateField;
    }
    
    @Override
    public String toString() {
        return "Step{" +
                "step=" + step +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", privateField='" + privateField + '\'' +
                '}';
    }
}
