package tfar.autoattack;

import com.google.gson.annotations.Expose;

public class Config {

    @Expose public boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }
    
    /* Return true if nothing was changed */
    public boolean validate() {
        boolean valid = true;
        return valid;
    }
}
