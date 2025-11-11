package com.platform.ems.util.data;

import cn.hutool.core.util.StrUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.util.LightUtil;

import java.util.Date;

import static com.platform.ems.util.LightUtil.*;

public class ComUtil {

    public static String[] strToArr(String path) {
        if (StrUtil.isNotBlank(path)) {
            return path.split(";");
        }
        return null;
    }

    public static String lightValue(String endStatus, Date planEndDate, Integer toexpireDaysScddSx) {
        // 空白
        String light = LIGHT_NULL;
        // 已完成
        if (ConstantsEms.END_STATUS_YWC.equals(endStatus) || ConstantsEms.COMPLETE_STATUS_YWG.equals(endStatus)) {
            light = LightUtil.LIGHT_BLUE;
        }
        // 暂搁
        else if (ConstantsEms.END_STATUS_ZG.equals(endStatus)) {
            light = LIGHT_GRY_ZG;
        }
        // 取消
        else if (ConstantsEms.END_STATUS_QX.equals(endStatus)) {
            light = LIGHT_GRY_QX;
        }
        else if (ConstantsEms.END_STATUS_JXZ.equals(endStatus) || ConstantsEms.END_STATUS_WKS.equals(endStatus)) {
            EmsBaseEntity entity = new EmsBaseEntity();
            LightUtil.setLight(entity, planEndDate, null, toexpireDaysScddSx);
            light = entity.getLight();
        }
        return light;
    }

}
