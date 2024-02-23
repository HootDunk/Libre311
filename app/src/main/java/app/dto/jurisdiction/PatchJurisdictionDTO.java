// Copyright 2023 Libre311 Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package app.dto.jurisdiction;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Introspected
public class PatchJurisdictionDTO {

    @NotEmpty
    private String name;

    @JsonProperty("primary_color")
    @Pattern(regexp = "(\\d+),?\\s?(\\.?\\d+%?),?\\s?(\\.?\\d+%?)")
    private String primaryColor;

    @JsonProperty("primary_hover_color")
    @Pattern(regexp = "(\\d+),?\\s?(\\.?\\d+%?),?\\s?(\\.?\\d+%?)")
    private String primaryHoverColor;

    @JsonProperty("logo_media_url")
    private String logoMediaUrl;

    private Set<LatLongDTO> bounds;

    public PatchJurisdictionDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getPrimaryHoverColor() {
        return primaryHoverColor;
    }

    public void setPrimaryHoverColor(String primaryHoverColor) {
        this.primaryHoverColor = primaryHoverColor;
    }

    public String getLogoMediaUrl() {
        return logoMediaUrl;
    }

    public void setLogoMediaUrl(String logoMediaUrl) {
        this.logoMediaUrl = logoMediaUrl;
    }

    public Set<LatLongDTO> getBounds() {
        return bounds;
    }

    public void setBounds(Set<LatLongDTO> bounds) {
        this.bounds = bounds;
    }
}
