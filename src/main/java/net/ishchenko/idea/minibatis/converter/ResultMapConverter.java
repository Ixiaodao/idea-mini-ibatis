package net.ishchenko.idea.minibatis.converter;


import com.google.common.collect.Lists;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.ResolvingConverter;
import net.ishchenko.idea.minibatis.model.sqlmap.IdDomElement;
import net.ishchenko.idea.minibatis.model.sqlmap.ResultMap;
import net.ishchenko.idea.minibatis.model.sqlmap.SqlMap;
import net.ishchenko.idea.minibatis.util.SqlMapperUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author jiwenbiao
 * @since : 2021/4/23 16:53
 */
public class ResultMapConverter extends ResolvingConverter<XmlAttributeValue> {
    @Override
    public @NotNull Collection<? extends XmlAttributeValue> getVariants(ConvertContext context) {
        SqlMap sqlMap = SqlMapperUtils.getMapperByDomElement(context.getInvocationElement());
        List<ResultMap> getSqlIds = sqlMap.getResultMaps();

        List<XmlAttributeValue> list = new ArrayList<>(20);
        List<IdDomElement> idDomElements = new ArrayList<>(getSqlIds);
        for (IdDomElement idDomElement : idDomElements) {
            list.add(idDomElement.getId().getXmlAttributeValue());
        }
        return list;
    }

    @Override
    public @Nullable XmlAttributeValue fromString(@Nullable String value, ConvertContext context) {
        List<IdDomElement> result = Lists.newArrayList();
        DomElement invocationElement = context.getInvocationElement();
        SqlMap mapper = SqlMapperUtils.getMapperByDomElement(invocationElement);
        result.addAll(mapper.getResultMaps());

        for (IdDomElement idDomElement : result) {
            if (StringUtils.equals(idDomElement.getId().getStringValue(), value)) {
                return idDomElement.getId().getXmlAttributeValue();
            }
        }
        return null;
    }

    @Override
    public @Nullable String toString(@Nullable XmlAttributeValue xmlAttributeValue, ConvertContext convertContext) {
        if (xmlAttributeValue == null) {
            return "";
        }
        return xmlAttributeValue.getValue();
    }

}
