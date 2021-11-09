package net.ishchenko.idea.minibatis.contributor;


import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.JavaLookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReference;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.ProcessingContext;
import com.intellij.util.Query;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import net.ishchenko.idea.minibatis.constant.IbatisConstant;
import net.ishchenko.idea.minibatis.model.sqlmap.GroupTwo;
import net.ishchenko.idea.minibatis.model.sqlmap.Sql;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 1
 * @author jinwenbiao
 * @since 2021/11/9 10:46
 */
public class WherePropertyParamContributor extends CompletionContributor {

    public WherePropertyParamContributor() {
        extend(CompletionType.BASIC,
                XmlPatterns.psiElement().inside(XmlPatterns.xmlTag().withName(IbatisConstant.ARR))
                        .inside(XmlPatterns.xmlAttribute().withName(IbatisConstant.PROPERTY)),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(
                            @NotNull final CompletionParameters parameters,
                            final ProcessingContext context,
                            @NotNull final CompletionResultSet result) {
                        addElementForPsiParameter2(parameters, context, result);
                    }
                });
    }

    private void addElementForPsiParameter2(CompletionParameters completionParameters, ProcessingContext context, CompletionResultSet result) {
        PsiElement originalPosition = completionParameters.getOriginalPosition();
        if (originalPosition == null) {
            return;
        }
        DomElement domElement = DomUtil.getDomElement(originalPosition);
        GroupTwo groupTwo = DomUtil.getParentOfType(domElement, GroupTwo.class, true); // select insert update delete
        if (groupTwo != null) {
            m1(result, groupTwo);
        } else {
            // 如果是引用的sql标签，根据sql标签找sql，然后找到调用sql的方法，再找到类里的属性进行提示
            Sql sql = DomUtil.getParentOfType(domElement, Sql.class, true);
            if (sql == null || sql.getId() == null || sql.getId().getXmlAttributeValue() == null) {
                return;
            }
            Query<PsiReference> psiReferences = ReferencesSearch.search(sql.getId().getXmlAttributeValue(), GlobalSearchScope.fileScope(originalPosition.getContainingFile()));
            Collection<PsiReference> psiReferenceCollection = psiReferences.findAll();
            for (PsiReference psiReference : psiReferenceCollection) { // 引用sql的地方
                PsiElement element = psiReference.getElement();
                DomElement includeDomElement = DomUtil.getDomElement(element);
                GroupTwo groupTwo2 = DomUtil.getParentOfType(includeDomElement, GroupTwo.class, true);
                if (groupTwo2 == null || groupTwo2.getParameterClass().getValue() == null) {
                    continue;
                }
                m1(result, groupTwo2);
                break;
            }
        }
    }

    private void m1(CompletionResultSet result, GroupTwo groupTwo) {
        GenericAttributeValue<PsiClass> parameterClass = groupTwo.getParameterClass();
        PsiClass psiClass = parameterClass.getValue();
        if (psiClass == null) {
            return;
        }
        PsiField[] allFields = psiClass.getAllFields();
        for (PsiField psiField : allFields) {
            addFieldTip(result, psiField);
        }
        result.stopHere();
    }

    private void addFieldTip(CompletionResultSet result, PsiField allField) {
        LookupElementBuilder lookupElementBuilder = JavaLookupElementBuilder.forField(allField);
        PsiDocComment docComment = allField.getDocComment();
        if (docComment != null) {
            String text = Arrays.stream(docComment.getDescriptionElements())
                    .filter(p -> p instanceof PsiDocToken)
                    .map(PsiElement::getText)
                    .collect(Collectors.joining());
            String trimmedText = text.trim();
            if (!StringUtils.isEmpty(trimmedText)) {
                trimmedText = "(" + trimmedText + ")";
                lookupElementBuilder = lookupElementBuilder.withTailText(trimmedText);
            }
        }
        result.addElement(lookupElementBuilder);
    }
}
