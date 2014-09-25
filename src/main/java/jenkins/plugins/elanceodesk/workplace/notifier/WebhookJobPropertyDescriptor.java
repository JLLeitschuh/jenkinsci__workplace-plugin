/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jenkins.plugins.elanceodesk.workplace.notifier;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.JobPropertyDescriptor;
import hudson.util.FormValidation;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Job Property Descriptor. 
 *
 */
@Extension
public final class WebhookJobPropertyDescriptor extends JobPropertyDescriptor {

    public WebhookJobPropertyDescriptor() {
        super(WebhookJobProperty.class);
        load();
    }

    private List<Webhook> webhooks = new ArrayList<Webhook>();

    public boolean isEnabled() {
        return !webhooks.isEmpty();
    }

    public List<Webhook> getTargets() {
        return webhooks;
    }

    public void setWebhooks(List<Webhook> webhooks) {
        this.webhooks = new ArrayList<Webhook>( webhooks );
    }

    @Override
    public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends Job> jobType) {
        return true;
    }

    @Override
    public String getDisplayName() {
        return "Job Notification";
    }

    public int getDefaultTimeout(){
        return Webhook.DEFAULT_TIMEOUT;
    }

    @Override
    public WebhookJobProperty newInstance(StaplerRequest req, JSONObject formData) throws FormException {

        List<Webhook> webhooks = new ArrayList<Webhook>();
        if (formData != null && !formData.isNullObject()) {
            JSON webhooksData = (JSON) formData.get("webhooks");
            if (webhooksData != null && !webhooksData.isEmpty()) {
                if (webhooksData.isArray()) {
                    JSONArray webhooksArrayData = (JSONArray) webhooksData;
                    webhooks.addAll(req.bindJSONToList(Webhook.class, webhooksArrayData));
                } else {
                    JSONObject webhooksObjectData = (JSONObject) webhooksData;
                    webhooks.add(req.bindJSON(Webhook.class, webhooksObjectData));
                }
            }
        }
        WebhookJobProperty notificationProperty = new WebhookJobProperty(webhooks);
        return notificationProperty;
    }

    public FormValidation doCheckUrl(@QueryParameter(value = "url", fixEmpty = true) String url) {
    	try {
			new URL(url);
		} catch (MalformedURLException e) {
			return FormValidation.error(e.getMessage());
		}
		return FormValidation.ok();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) {
        save();
        return true;
    }

}