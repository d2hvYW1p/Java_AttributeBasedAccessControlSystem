package com.whoami.attributebaseaccesscontrol.Utils;

import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.finder.AttributeFinderModule;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sample attribute finder module
 */
public class SampleAttributeFinderModule extends AttributeFinderModule {

    private URI defaultSubjectId;

    public SampleAttributeFinderModule() {

        try {
            defaultSubjectId = new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
        } catch (URISyntaxException e) {
            //ignore
        }

    }

    @Override
    public Set<String> getSupportedCategories() {
        Set<String> categories = new HashSet<String>();
        categories.add("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
        return categories;
    }


    @Override
    public Set getSupportedIds() {
        Set<String> ids = new HashSet<String>();
        ids.add("http://wso2.org/attribute/roleNames");
        return ids;
    }

    /**
     * Function that find's attribute from an xacml format( More specifically maps the role to a user)
     *
     * @param attributeType
     * @param attributeId
     * @param issuer
     * @param category
     * @param context
     * @return
     */
    @Override
    public EvaluationResult findAttribute(URI attributeType, URI attributeId, String issuer,
                                          URI category, EvaluationCtx context) {
        String roleName = null;
        List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();

        EvaluationResult result = context.getAttribute(attributeType, defaultSubjectId, issuer, category);
        if (result != null && result.getAttributeValue() != null && result.getAttributeValue().isBag()) {
            BagAttribute bagAttribute = (BagAttribute) result.getAttributeValue();
            if (bagAttribute.size() > 0) {
                String userName = ((AttributeValue) bagAttribute.iterator().next()).encode();

                roleName = findRole(userName);
            }
        }

        if (roleName != null) {
            attributeValues.add(new StringAttribute(roleName));
        }

        return new EvaluationResult(new BagAttribute(attributeType, attributeValues));
    }

    @Override
    public boolean isDesignatorSupported() {
        return true;
    }

    /**
     * Maps A role According to username(subject)
     *
     * @param userName
     * @return
     */
    private String findRole(String userName) {

        if (userName.equals("user")) {
            return "publicUsers";
        } else if (userName.equals("employee")) {
            return "internalUsers";
        } else if (userName.equals("admin")) {
            return "adminUsers";
        }

        return null;
    }
}
