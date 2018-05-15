package model;

import java.io.Serializable;

public class ReportLicense implements Serializable {
    /**
     * Indicates the name of the license found for the dependency
     * e.g MIT License
     */
    private String name;

    /**
     * Indicates where was the license found, the strategy that successfully found the license
     * <ul>
     *     <li>License found from the tag <b>Licenses</b> of the Pom file </li>
     *     <li>License found as a commentary in the Pom File by searching for keywords indicating a license e.g. http://www.apache.org/licenses/LICENSE-2.0 for Apache Version 2.0</li>
     *     <li>License found assessing the <b>LICENSE</b> file for a keywords indicating a license e.g. http://www.apache.org/licenses/LICENSE-2.0 for Apache Version 2.0 </li>
     * </ul>
     */
    private String origins;

    public String getName() {
        return name;
    }

    public String getOrigins() {
        return origins;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrigins(String origins) {
        this.origins = origins;
    }
}
