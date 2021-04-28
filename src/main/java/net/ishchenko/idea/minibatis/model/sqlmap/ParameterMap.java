package net.ishchenko.idea.minibatis.model.sqlmap;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import net.ishchenko.idea.minibatis.converter.AliasConverter;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 02.03.12
 * Time: 21:44
 */
public interface ParameterMap extends IdDomElement {

    @Attribute("class")
    @Convert(AliasConverter.class)
    GenericAttributeValue<TypeAlias> getClazz();

}
