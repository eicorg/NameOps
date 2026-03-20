package gov.bnl.eic.nameops.model;

/**
 * Represents a naming element (area, device, signal, etc.) with its abbreviation and full name
 */
public class NamingElement {
    private String abbreviation;
    private String fullName;
    private String description;
    private String latticeKeyword;

    public NamingElement() {
    }

    public NamingElement(String abbreviation, String fullName, String description) {
        this.abbreviation = abbreviation;
        this.fullName = fullName;
        this.description = description;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatticeKeyword() {
        return latticeKeyword;
    }

    public void setLatticeKeyword(String latticeKeyword) {
        this.latticeKeyword = latticeKeyword;
    }

    @Override
    public String toString() {
        return abbreviation + " (" + fullName + ")";
    }
}
