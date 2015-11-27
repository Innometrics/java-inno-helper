package com.innometrics.integrationapp;

import com.innometrics.integrationapp.appsettings.FieldSetsEntry;
import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.model.Attribute;
import com.innometrics.integrationapp.model.Event;
import com.innometrics.integrationapp.model.Profile;
import com.innometrics.integrationapp.utils.InnoHelperUtils;
import jdk.nashorn.internal.ir.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by killpack on 26.11.15.
 */
public class InnoTransformer {
    Map<String, RulesEntry> rulesEntries = new HashMap<>();
    public static final String
            PROFILE_ATTRIBUTE = "profileAttribute",
            PROFILE_ID = "profileId",
            EVENT_VALUE = "eventValue",
            SESSION_VALUE = "sessionValue",
            STATIC_VALUE = "static",
            MACRO_VALUE = "macro",
            META_VALUE = "meta";
    static final String
            MARCO_TIMESTAMP_NOW = "timestamp_now",
            MARCO_TIMESTAMP_EVENT = "timestamp_event",
            MARCO_REQUEST_IP = "request_ip",
            MARCO_USER_AGENT = "user_agent";
    private static final String
            META_PROFILE_ID = "profileId",
            META_COMPANY_ID = "company_id",
            META_BUCKET_ID = "bucket_id",
            META_EVENT_DEFINITION_ID = "event_def_id",
            META_APP_SECTION_EVENT = "app_section_event",
            META_COLLECT_APP = "collect_app",
            META_SECTION = "section";

    public static final String MAPPING_SET_NAME = "mapping";


    //
//    /**
//     * Main access point for Marco Value
//     *
//     * @param valueLocation
//     * @param requestMeta
//     * @param event
//     * @return
//     */
//    private static JsonNode getMarcoValue(String valueLocation, RequestMeta requestMeta, Event event) {
//        switch (valueLocation) {
//            case MARCO_REQUEST_IP:
//                return getObjectMapper().getNodeFactory().textNode(requestMeta.requestIp);
//            case MARCO_USER_AGENT:
//                return getObjectMapper().getNodeFactory().textNode(requestMeta.headers.get("User-Agent"));
//            default:
//                return getMarcoValue(valueLocation, event);
//        }
//    }
//
//    private static JsonNode getMarcoValue(String valueLocation, Event event) {
//        switch (valueLocation) {
//            case MARCO_TIMESTAMP_NOW:
//                return getObjectMapper().getNodeFactory().numberNode(System.currentTimeMillis());
//            case MARCO_TIMESTAMP_EVENT:
//                return getObjectMapper().getNodeFactory().numberNode(event.getCreatedAt().getTime());
//            default:
//                return null;
//        }
//    }
//
//    /**
//     * Main access point for meta Value
//     *
//     * @param valueLocation
//     * @param queueEvent
//     * @return
//     */
//    private static JsonNode getMetaValue(String valueLocation, QueueEvent queueEvent) {
//        switch (valueLocation) {
//            case META_COLLECT_APP:
//                return getObjectMapper().getNodeFactory().textNode(queueEvent.session.getCollectApp());
//            case META_SECTION:
//                return getObjectMapper().getNodeFactory().textNode(queueEvent.session.getSection());
//            case META_PROFILE_ID:
//                return getObjectMapper().getNodeFactory().textNode(queueEvent.profile.getId());
//            case META_APP_SECTION_EVENT:
//                return getObjectMapper().getNodeFactory().textNode(queueEvent.internalEventListenerKey);
//            default:
//                return getMetaValue(valueLocation, queueEvent.meta.companyId, queueEvent.meta.bucketId, queueEvent.event);
//        }
//    }
//
//    private static JsonNode getMetaValue(String valueLocation, String companyId, String bucketId, Event event) {
//        switch (valueLocation) {
//            case META_COMPANY_ID:
//                return getObjectMapper().getNodeFactory().textNode(companyId);
//            case META_BUCKET_ID:
//                return getObjectMapper().getNodeFactory().textNode(bucketId);
//            case META_EVENT_DEFINITION_ID:
//                return getObjectMapper().getNodeFactory().textNode(event.getDefinitionId());
//            default:
//                return null;
//        }
//    }
//
    public InnoTransformer(RulesEntry[] rulesEntries) {
        for (RulesEntry rulesEntry : rulesEntries) {
            this.rulesEntries.put(rulesEntry.getEvent(), rulesEntry);
        }
    }

    public InnoTransformer(InnoHelper innoHelper) throws ExecutionException, InterruptedException {
        this(innoHelper.getCustom("rules", RulesEntry[].class));
    }

    public Map<String, Object> fromProfile(Profile profile) {
        Map<String, Object> result = new HashMap<>();
        RulesEntry rulesEntry = rulesEntries.get(InnoHelperUtils.getFullFirstEventName(profile));
        if (rulesEntry == null) {
            return result;
        }
        final List<Attribute> attributes = profile.getAttributes();
        final Map<String, Object> sessionData = profile.getSessions().get(0).getData();
        final Event event = profile.getSessions().get(0).getEvents().get(0);

//
        for (FieldSetsEntry fieldSet : rulesEntry.getFieldSets()) {
            String outGoingFieldName = fieldSet.getSetName();
            if (outGoingFieldName.equals(MAPPING_SET_NAME)) {
                for (FieldsEntry field : fieldSet.getFields()) {
                    if (field == null || field.getType() == null)
                        throw new CrossSystemMessageProcessingException(String.format("Field or field type is null for rule %s [%s]", rulesEntry.getName(), rulesEntry.getId()));
                    switch (field.getType()) {
                        case PROFILE_ATTRIBUTE:
                            result.put(field.getFieldName(), getUserAttribute(attributes, getValueRef(field)));
                            break;
                        case PROFILE_ID:
                            result.put(field.getFieldName(), profile.getId());
                            break;
                        case EVENT_VALUE:
                            result.put(field.getFieldName(), getEventValue(event, getValueRef(field)));
                            break;
                        case SESSION_VALUE:
                            result.put(field.getFieldName(), sessionData.get(getValueRef(field)));
                            break;
                        case STATIC_VALUE:
                            result.put(field.getFieldName(), field.getValueRef());
                            break;
                        case MACRO_VALUE:
                            result.put(field.getFieldName(), getMarcoValue(getValueRef(field), meta.request, event));
                            break;
                        case META_VALUE:
                            //TODO: to be fixed
                            result.put(field.getFieldName(), getMetaValue(getValueRef(field), queueEvent));
                            break;
                        default:
//                        throw new UnsupportedFiledValueTypeException("com.innometrics.model.companyId.app.FieldsEntry[type]=" + field.getType() + " Not supported");
                    }
                }
            }
            // todo if need transform all fieldSets
//            if (result.size() > 0) {
//                msg.addField(outGoingFieldName, toAdd);
//            }
        }
        return result;
    }

    public Profile toProfile(Map<String, Object> map) {
        Profile profile = new Profile();

        return profile;
    }

    private static Object getUserAttribute(List<Attribute> attributes, String valueLocation) {
        String[] layers = valueLocation.split("/");
        for (Attribute attribute : attributes) {
            if (attribute.getCollectApp().equals(layers[0]) && attribute.getSection().equals(layers[1])) {
                return attribute.getData().get(layers[2]);
            }
        }
        return null;
    }

    private static String getValueRef(FieldsEntry field) {
        if (field.getValueRef() == null)
            throw new CrossSystemMessageProcessingException(String.format("ValueRef is missing for field: %s", field.getFieldName()));
        return field.getValueRef().asText();
    }

    private static Object getEventValue(Event input, String valueLocation) {
        return input.getData().get(valueLocation);
    }
    private static JsonNode getMetaValue(String valueLocation) {
        switch (valueLocation) {
            case META_COLLECT_APP:
                return getObjectMapper().getNodeFactory().textNode(queueEvent.session.getCollectApp());
            case META_SECTION:
                return getObjectMapper().getNodeFactory().textNode(queueEvent.session.getSection());
            case META_PROFILE_ID:
                return getObjectMapper().getNodeFactory().textNode(queueEvent.profile.getId());
            case META_APP_SECTION_EVENT:
                return getObjectMapper().getNodeFactory().textNode(queueEvent.internalEventListenerKey);
            default:
                return getMetaValue(valueLocation, queueEvent.meta.companyId, queueEvent.meta.bucketId, queueEvent.event);
        }
    }
}
