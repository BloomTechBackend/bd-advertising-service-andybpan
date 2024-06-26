package com.amazon.ata.advertising.service.targeting;

import com.amazon.ata.advertising.service.model.RequestContext;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicate;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Evaluates TargetingPredicates for a given RequestContext.
 */
public class TargetingEvaluator {
    public static final boolean IMPLEMENTED_STREAMS = true;
    public static final boolean IMPLEMENTED_CONCURRENCY = true;
    private final RequestContext requestContext;

    /**
     * Creates an evaluator for targeting predicates.
     * @param requestContext Context that can be used to evaluate the predicates.
     */
    public TargetingEvaluator(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    /**
     * Evaluate a TargetingGroup to determine if all of its TargetingPredicates are TRUE or not for the given
     * RequestContext.
     * @param targetingGroup Targeting group for an advertisement, including TargetingPredicates.
     * @return TRUE if all of the TargetingPredicates evaluate to TRUE against the RequestContext, FALSE otherwise.
     */
    public TargetingPredicateResult evaluate(TargetingGroup targetingGroup) {
        // we get the list of predicate
        // evaluate based on context
        // checks if all the values are true to return true
        // otherwise return false

        ExecutorService executor = Executors.newCachedThreadPool();
        TargetingPredicateResult result = targetingGroup.getTargetingPredicates().stream()
                .map(predicate -> executor.submit(() -> predicate.evaluate(requestContext)))
                .map(resultFuture -> {
                    try {
                        return resultFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException("predicate Result failed to get", e);
                    }
                })
                .allMatch(TargetingPredicateResult::isTrue)
                ? TargetingPredicateResult.TRUE :
                TargetingPredicateResult.FALSE;
        executor.shutdown();
        return result;

        // Sprint 25 Version
//        TargetingPredicateResult result = targetingGroup.getTargetingPredicates().stream()
//                .map(predicate -> predicate.evaluate(requestContext))
//                .allMatch(TargetingPredicateResult::isTrue)
//                ? TargetingPredicateResult.TRUE :
//                TargetingPredicateResult.FALSE;
//        return result;


        // Original implementation
//        List<TargetingPredicate> targetingPredicates = targetingGroup.getTargetingPredicates();
//        boolean allTruePredicates = true;
//        for (TargetingPredicate predicate : targetingPredicates) {
//            TargetingPredicateResult predicateResult = predicate.evaluate(requestContext);
//            if (!predicateResult.isTrue()) {
//                allTruePredicates = false;
//                break;
//            }
//        }
//
//        return allTruePredicates ? TargetingPredicateResult.TRUE :
//                                   TargetingPredicateResult.FALSE;
    }
}
