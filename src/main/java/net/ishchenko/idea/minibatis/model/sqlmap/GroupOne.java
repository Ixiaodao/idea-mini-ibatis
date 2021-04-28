package net.ishchenko.idea.minibatis.model.sqlmap;


import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author jiwenbiao
 * @since : 2021/4/28 18:31
 */
public interface GroupOne extends DomElement {
    @NotNull
    @SubTagList("include")
    List<Include> getIncludes();

    @NotNull
    @SubTagList("isEmpty")
    List<IsEmpty> getIsEmpty();

    @NotNull
    @SubTagList("isEqual")
    List<IsEqual> getIsEqual();

    @NotNull
    @SubTagList("isNotEmpty")
    List<IsNotEmpty> getIsNotEmpty();

    @NotNull
    @SubTagList("isNotEqual")
    List<IsNotEqual> getIsNotEqual();
}
