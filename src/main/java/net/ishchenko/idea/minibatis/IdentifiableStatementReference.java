package net.ishchenko.idea.minibatis;


import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.PsiPolyadicExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.JavaConstantExpressionEvaluator;
import com.intellij.psi.impl.PomTargetPsiElementImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.xml.DomTarget;
import net.ishchenko.idea.minibatis.model.sqlmap.IdDomElement;
import net.ishchenko.idea.minibatis.model.sqlmap.SqlMap;
import net.ishchenko.idea.minibatis.util.DomUtils;
import net.ishchenko.idea.minibatis.util.Icons;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 24.12.11
 * Time: 23:59
 */
public class IdentifiableStatementReference extends PsiPolyVariantReferenceBase<PsiLiteral> {

    public IdentifiableStatementReference(PsiLiteral expression) {
        super(expression);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean b) {
        String value = getNamespaceAndMethod();
        if (value.length() == 0) {
            return ResolveResult.EMPTY_ARRAY;
        }
        if (!value.contains(".") || value.endsWith(".")) {
            return ResolveResult.EMPTY_ARRAY;
        }
        String[] arr = value.split("\\.");
        String id = arr[arr.length - 1];
        String namespace = StringUtils.removeEnd(value, "." + id);
        List<ResolveResult> results = findResults2V(namespace, id);
        if (CollectionUtils.isEmpty(results)) {
            return ResolveResult.EMPTY_ARRAY;
        }
        return results.toArray(new ResolveResult[results.size()]);

    }

    private List<ResolveResult> findResults2V(String namespace, String id) {
        List<ResolveResult> resultList = new ArrayList<>();
        Collection<SqlMap> domElements = DomUtils.findDomElements(getElement().getProject(), SqlMap.class);
        for (SqlMap domElement : domElements) {
            String stringValue = domElement.getNamespace().getStringValue();
            if (!Objects.equals(namespace, stringValue)) {
                continue;
            }
            List<IdDomElement> identifiableStatements = domElement.getDaoElements();
            for (IdDomElement idDomElement : identifiableStatements) {
                DomTarget target = DomTarget.getTarget(idDomElement);
                String methodName = idDomElement.getId().getStringValue();
                if (Objects.equals(methodName, id)) {
                    resultList.add(new PsiElementResolveResult(new PomTargetPsiElementImpl(target) {
                        @Override
                        public Icon getIcon() {
                            return Icons.XML_TAG_ICON;
                        }
                        @Override
                        public String getLocationString() {
                            return methodName;
                        }
                    }));
                }

            }
        }
        return resultList;
    }

    @NotNull
    public Object[] getVariants() {
        return EMPTY_ARRAY;
        //CommonProcessors.CollectProcessor<String> processor = new CommonProcessors.CollectProcessor<String>();
        //ServiceManager.getService(getElement().getProject(), DomFileElementsFinder.class).processSqlMapStatementNames(processor);
        //return processor.toArray(new String[processor.getResults().size()]);
    }

    private String getNamespaceAndMethod() {

        PsiPolyadicExpression parentExpression = PsiTreeUtil.getParentOfType(getElement(), PsiPolyadicExpression.class);

        if (parentExpression != null) {
            StringBuilder computedValue = new StringBuilder();
            for (PsiExpression operand : parentExpression.getOperands()) {
                if (operand instanceof PsiReference) {
                    PsiElement probableDefinition = ((PsiReference) operand).resolve();
                    if (probableDefinition instanceof PsiVariable) {
                        PsiExpression initializer = ((PsiVariable) probableDefinition).getInitializer();
                        if (initializer != null) {
                            Object value = JavaConstantExpressionEvaluator.computeConstantExpression(initializer, true);
                            if (value instanceof String) {
                                computedValue.append(value);
                            }
                        }
                    }
                } else {
                    Object value = JavaConstantExpressionEvaluator.computeConstantExpression(operand, true);
                    if (value instanceof String) {
                        computedValue.append(value);
                    }
                }
            }
            return computedValue.toString();
        } else {
            String rawText = getElement().getText();
            //with quotes, i.e. at least "x" count
            if (rawText.length() < 3) {
                return "";
            }
            //clean up quotes
            return rawText.substring(1, rawText.length() - 1);
        }

    }

}
