package org.jboss.scripts;

/**
 * A {@link ScriptModel} which holds some meta-data.
 *
 * @author <a href="mailto:thomas.darimont@gmail.com">Thomas Darimont</a>
 */
public class ScriptModel {

    /**
     * MIME-Type for JavaScript
     */
    public static final String TEXT_JAVASCRIPT = "text/javascript";

    private String id;

    private String realmId;

    private String name;

    private String mimeType;

    private String code;

    private String description;

    public ScriptModel(String id, String realmId, String name, String mimeType, String code, String description) {

        this.id = id;
        this.realmId = realmId;
        this.name = name;
        this.mimeType = mimeType;
        this.code = code;
        this.description = description;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getRealmId() {
        return realmId;
    }

    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Script{" +
                "id='" + id + '\'' +
                ", realmId='" + realmId + '\'' +
                ", name='" + name + '\'' +
                ", type='" + mimeType + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

