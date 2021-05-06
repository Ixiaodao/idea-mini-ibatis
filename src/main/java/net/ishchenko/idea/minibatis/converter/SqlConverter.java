package net.ishchenko.idea.minibatis.converter;


import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.ResolvingConverter;
import net.ishchenko.idea.minibatis.model.sqlmap.IdDomElement;
import net.ishchenko.idea.minibatis.model.sqlmap.Sql;
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
public class SqlConverter extends ResolvingConverter<XmlAttributeValue> {
    public SqlConverter() {
    }

    @Override
    public @NotNull Collection<? extends XmlAttributeValue> getVariants(ConvertContext context) {
        DomElement invocationElement = context.getInvocationElement();
        SqlMap mapper = SqlMapperUtils.getMapperByDomElement(invocationElement);
        List<Sql> sqls = mapper.getSqls();
        List<IdDomElement> result = new ArrayList<>(sqls);
        List<XmlAttributeValue> list = new ArrayList<>(20);
        for (IdDomElement idDomElement : result) {
            list.add(idDomElement.getId().getXmlAttributeValue());
        }
        return list;
    }

    @Override
    public @Nullable XmlAttributeValue fromString(@Nullable String value, ConvertContext context) {
        DomElement invocationElement = context.getInvocationElement();
        SqlMap mapper = SqlMapperUtils.getMapperByDomElement(invocationElement);
        List<Sql> sqls = mapper.getSqls();
        List<IdDomElement> result = new ArrayList<>(sqls);
        for (IdDomElement idDomElement : result) {
            String id = idDomElement.getId().getStringValue();
            if (StringUtils.equals(id, value)) {
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
