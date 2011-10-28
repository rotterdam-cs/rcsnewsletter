
package com.rcs.newsletter.util;

import com.rcs.newsletter.core.dto.DataTransferObject;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public class JsonDTOBuilder extends JsonBuilder {
    
    private DataTransferObject data;

    public DataTransferObject getData() {
        return data;
    }

    public void setData(DataTransferObject data) {
        this.data = data;
    }
    
}
