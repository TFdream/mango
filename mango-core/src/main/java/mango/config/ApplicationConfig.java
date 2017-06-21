package mango.config;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ApplicationConfig extends AbstractConfig {

    private static final long serialVersionUID = 3706130733051761098L;
    private String name;    //当前应用名称，必填
    private String version;
    private String manager;    //负责人
    private String organization;  //组织名称(BU或部门)
    private String env;    //应用环境，如：develop/test/product
    private Boolean isDefault = Boolean.TRUE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
