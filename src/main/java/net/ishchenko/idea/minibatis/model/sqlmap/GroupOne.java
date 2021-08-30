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
    @SubTagList("isNull")
    List<IsEqual> getIsNull();

    @NotNull
    @SubTagList("isNotEmpty")
    List<IsNotEmpty> getIsNotEmpty();

    @NotNull
    @SubTagList("isNotEqual")
    List<IsNotEqual> getIsNotEqual();

    @NotNull
    @SubTagList("isNotNull")
    List<IsNotEqual> getIsNotNull();

    @NotNull
    @SubTagList("iterate")
    List<Iterate> getIterate();

    @NotNull
    @SubTagList("dynamic")
    List<Dynamic> getDynamic();

    @NotNull
    @SubTagList("isParameterPresent")
    List<IsParameterPresent> getIsParameterPresent();

    @NotNull
    @SubTagList("isNotParameterPresent")
    List<IsNotParameterPresent> getIsNotParameterPresent();

    @NotNull
    @SubTagList("isGreaterThan")
    List<IsGreaterThan> getIsGreaterThan();

    @NotNull
    @SubTagList("isGreaterEqual")
    List<IsGreaterEqual> getIsGreaterEqual();
    @NotNull
    @SubTagList("isLessThan")
    List<IsLessThan> getIsLessThan();
    @NotNull
    @SubTagList("isLessEqual")
    List<IsLessEqual> getIsLessEqual();
    @NotNull
    @SubTagList("isPropertyAvailable")
    List<IsPropertyAvailable> getIsPropertyAvailable();
    @NotNull
    @SubTagList("isNotPropertyAvailable")
    List<IsNotPropertyAvailable> getIsNotPropertyAvailable();
}
