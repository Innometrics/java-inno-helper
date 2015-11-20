package com.innometrics.integrationapp.utils;

import com.innometrics.integrationapp.model.Profile;
import com.innometrics.iql.IqlExecutor;
import com.innometrics.iql.IqlResult;
import com.innometrics.iql.IqlSyntaxException;
import static com.innometrics.integrationapp.InnoHelperUtils.*;

/**
 * @author andrew, Innometrics
 */
public class SegmentUtil {

    public static IqlResult getIqlResult(String iql, Profile profile, boolean filter) throws IqlSyntaxException {
        IqlExecutor executor = new IqlExecutor(iql);
        executor.setFiltrate(filter);
        String profileAsString = "{\"profile\":" + getGson().toJson(profile) + "}";
        return executor.execute(profileAsString);
    }
}
