package net.ishchenko.idea.minibatis.model.sqlmap;


import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.SubTagList;
import net.ishchenko.idea.minibatis.converter.AliasConverter;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 23.12.11
 * Time: 23:43
 */
public interface SqlMapIdentifiableStatement extends DomElement {

    @NameValue
    @Attribute("id")
    GenericAttributeValue<String> getId();

    @Attribute("parameterClass")
    @Convert(AliasConverter.class)
    GenericAttributeValue<TypeAlias> getParameterClass();

    @Attribute("resultClass")
    @Convert(AliasConverter.class)
    GenericAttributeValue<TypeAlias> getResultClass();

    @Attribute("parameterMap")
    @Convert(AliasConverter.class)
    GenericAttributeValue<ParameterMap> getParameterMap();

    @SubTagList("include")
    List<Include> getIncludes();

}
