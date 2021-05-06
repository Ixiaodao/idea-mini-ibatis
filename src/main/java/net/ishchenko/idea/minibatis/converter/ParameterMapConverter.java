package net.ishchenko.idea.minibatis.converter;


import com.google.common.collect.Lists;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.ResolvingConverter;
import net.ishchenko.idea.minibatis.model.sqlmap.IdDomElement;
import net.ishchenko.idea.minibatis.model.sqlmap.ParameterMap;
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
 * @since : 2021/4/28 10:00
 */
public class ParameterMapConverter extends ResolvingConverter<XmlAttributeValue>  {
    @Override
    public @NotNull Collection<? extends XmlAttributeValue> getVariants(ConvertContext context) {
        SqlMap sqlMap = SqlMapperUtils.getMapperByDomElement(context.getInvocationElement());
        List<ParameterMap> getSqlIds = sqlMap.getParameterMap();
        List<IdDomElement> idDomElementList = new ArrayList<>(getSqlIds);
        List<XmlAttributeValue> list = new ArrayList<>(20);
        for (IdDomElement idDomElement : idDomElementList) {
            list.add(idDomElement.getId().getXmlAttributeValue());
        }
        return list;
    }

    @Override
    public @Nullable XmlAttributeValue fromString(@Nullable String value, ConvertContext context) {
        List<IdDomElement> result = Lists.newArrayList();
        DomElement invocationElement = context.getInvocationElement();
        SqlMap mapper = SqlMapperUtils.getMapperByDomElement(invocationElement);
        result.addAll(mapper.getParameterMap());

        for (IdDomElement idDomElement : result) {
            if (StringUtils.equals(idDomElement.getId().getStringValue(), value)) {
                return idDomElement.getId().getXmlAttributeValue();
            }
        }
        return null;
    }

    @Override
    public @Nullable String toString(@Nullable XmlAttributeValue xmlAttributeValue, ConvertContext convertContext) {
        return xmlAttributeValue != null ? xmlAttributeValue.getValue() : "";
    }

}
