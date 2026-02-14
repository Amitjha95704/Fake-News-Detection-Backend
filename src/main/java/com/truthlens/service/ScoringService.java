//package com.truthlens.service;
//
//import com.truthlens.model.response.VerdictType;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ScoringService {
//
//    public int calculateConfidence(int support,
//                                   int contradict,
//                                   int partial,
//                                   int unrelated) {
//
//        int relevant = support + contradict + partial;
//
//        if (relevant == 0) {
//            return 50; // neutral confidence
//        }
//
//        int dominant = Math.max(support, contradict);
//
//        double ratio = (double) dominant / relevant;
//
//        return (int) Math.round(ratio * 100);
//    }
//
//    public VerdictType determineVerdict(int support,
//                                        int contradict,
//                                        int partial,
//                                        int unrelated) {
//
//        int relevant = support + contradict + partial;
//
//        if (relevant == 0) {
//            return VerdictType.UNVERIFIED;
//        }
//
//        if (support > contradict && support >= partial) {
//            return VerdictType.LIKELY_TRUE;
//        }
//
//        if (contradict > support && contradict >= partial) {
//            return VerdictType.LIKELY_FALSE;
//        }
//
//        if (partial > support && partial > contradict) {
//            return VerdictType.MISLEADING;
//        }
//
//        // Mixed case fallback
//        return VerdictType.MISLEADING;
//    }
//}

package com.truthlens.service;

import com.truthlens.model.response.VerdictType;
import org.springframework.stereotype.Service;

@Service
public class ScoringService {

    public int calculateConfidence(int support,
                                   int contradict,
                                   int partial,
                                   int unrelated) {

        int relevant = support + contradict + partial;

        if (relevant == 0) {
            return 50;
        }

        if (support > contradict) {
            return (int) ((double) support / relevant * 100);
        } else {
            return (int) ((double) contradict / relevant * 100);
        }
    }

//    public VerdictType determineVerdict(int support,
//                                        int contradict,
//                                        int partial,
//                                        int unrelated) {
//
//        int relevant = support + contradict + partial;
//
//        if (relevant == 0) {
//            return VerdictType.UNVERIFIED;
//        }
//
//        if (support > contradict && support >= partial) {
//            return VerdictType.LIKELY_TRUE;
//        }
//
//        if (contradict > support && contradict >= partial) {
//            return VerdictType.LIKELY_FALSE;
//        }
//
//        return VerdictType.MISLEADING;
//    }
    public VerdictType determineVerdict(int support,
            int contradict,
            int partial,
            int unrelated) {

int relevant = support + contradict + partial;

if (relevant == 0) {
return VerdictType.UNVERIFIED;
}

if (support > contradict && support >= partial) {
return VerdictType.LIKELY_TRUE;
}

if (contradict > support && contradict >= partial) {
return VerdictType.LIKELY_FALSE;
}

if (partial > support && partial > contradict) {
return VerdictType.MISLEADING;
}

return VerdictType.MISLEADING;
}

}
