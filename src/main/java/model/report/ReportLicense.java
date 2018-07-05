package model.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class ReportLicense implements Serializable {
    /**
     * Indicates the name of the license found for the dependency
     * e.g MIT License
     */
    @JsonProperty(value = "spdx_id")
    private String spdxId = "";

    /**
     * Indicates where was the license found, the strategy that successfully found the license
     * <ul>
     *     <li>License found from the tag <b>Licenses</b> of the Pom file </li>
     *     <li>License found as a commentary in the Pom File by searching for keywords indicating a license e.g. http://www.apache.org/licenses/LICENSE-2.0 for Apache Version 2.0</li>
     *     <li>License found assessing the <b>LICENSE</b> file for a keywords indicating a license e.g. http://www.apache.org/licenses/LICENSE-2.0 for Apache Version 2.0 </li>
     * </ul>
     */
    private String source = "";

    /**
     * Indicates if the license is valid considering the policy indicated in the project.
     */
    private boolean valid;

    public String getSpdxId() {
        return spdxId;
    }

    public String getSource() {
        return source;
    }

    public boolean isValid() {
        return valid;
    }

    public void setSpdxId(String spdxId) {
        this.spdxId = spdxId;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return String.format("License name %s, found in %s", spdxId, source);
    }
}
