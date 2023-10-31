import pandas as pd
from django.shortcuts import render
from rest_framework import viewsets
from rest_framework.decorators import api_view
from rest_framework.response import Response
from .models import Problem, ProblemTag, TagCorrelation, BaekjoonUser, SolvedProblemHistory, RecommendProblem, User
from sklearn.preprocessing import StandardScaler
from sklearn.neighbors import NearestNeighbors
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel
from .serializers import ProblemSerializer, ProblemTagSerializer
from collections import defaultdict
from django.db import transaction
from itertools import combinations
from scipy.stats import pearsonr
from random import sample
import numpy as np
from scipy.cluster.hierarchy import linkage, fcluster

#유저정보request
@api_view(['POST'])
def test_recommend_problem(request):
    RecommendProblem.objects.all().delete()
    all_users = User.objects.all()
    users = BaekjoonUser.objects.only('bj_id', 'tier', 'solved_count', 'rating')
    data = np.array([(user.tier, user.solved_count, user.rating) for user in users])

    # 계층적 클러스터링 수행
    linkage_matrix = linkage(data, method='ward')

    # 클러스터 자르기 및 결과 출력
    k = 3  # 클러스터의 수
    cluster_labels = fcluster(linkage_matrix, k, criterion='maxclust')
    # print("클러스터 레이블:", cluster_labels)

    # 클러스터별 데이터 묶어서 출력 (인덱스 번호와 bj_id 포함)
    clusters = {}
    bj_ids = [user.bj_id for user in users]
    for i, label in enumerate(cluster_labels):
        if label not in clusters:
            clusters[label] = []
        clusters[label].append((i, bj_ids[i], data[i]))


    # 1. 강약점 추천
    for user in all_users:
        bj_user_data = BaekjoonUser.objects.get(bj_id = user.solved_ac_id)
        user_data = User.objects.get(solved_ac_id = bj_user_data.bj_id)
        #유저가 푼 상위100문제정보
        bj_user_solved_problems = SolvedProblemHistory.objects.filter(user=user_data.user_id).select_related('problem').order_by('-problem__problem_level', '-problem__solved_user_count')[:100]

        # 유저가 푼문제에 대한 태크 딕셔너리
        solved_problems_tags_dict = defaultdict(set)
        for problem in bj_user_solved_problems:
            problem_id = problem.problem_id
            solved_problem_tags = ProblemTag.objects.filter(problem_id = problem_id)
            for tag in solved_problem_tags:
                solved_problems_tags_dict[problem_id].add(tag.tag_id)
            # solved_problems_tags_dict[problem_id].add(solved_problem_tags.values_list('tag_id', flat=True))

        # 유저티어정보
        user_tier_data = bj_user_data.tier
        # 유저레벨에적합한 레벨문제 1차 필터링
        min_tier = max(user_tier_data-5,1)
        max_tier = min(user_tier_data+2,30)
        first_filtered_problems_strong = Problem.objects.filter(problem_level__range=[user_tier_data,max_tier])
        if user_tier_data > 2:
            first_filtered_problems_weak = Problem.objects.filter(problem_level__range=[min_tier, user_tier_data])
        else:
            first_filtered_problems_weak = Problem.objects.filter(problem_level__in=[1])
        print(first_filtered_problems_strong)
        print('-----------------------------------------')
        print(first_filtered_problems_weak)
        # 강점 1차필터링 된 문제들의태그들
        first_filtered_problems_tags_dict = defaultdict(set)
        for problem in first_filtered_problems_strong:
            problem_id = problem.problem_id
            problem_tags = ProblemTag.objects.filter(problem_id = problem_id)
            first_filtered_problems_tags_dict[problem_id].add(problem_tags.values_list('tag_id', flat=True))
        # 약점 1차필터링 된 문제들의태그들
        first_filtered_problems_tags_dict2 = defaultdict(set)
        for problem in first_filtered_problems_weak:
            problem_id = problem.problem_id
            problem_tags = ProblemTag.objects.filter(problem_id = problem_id)
            first_filtered_problems_tags_dict2[problem_id].add(problem_tags.values_list('tag_id', flat=True))

        recommended_problems = []
        # 유사성 측정 및 추천
        for problem_index, _ in first_filtered_problems_tags_dict.items():
            # 유저가 이미 푼 문제는 스킵
            if problem_index in solved_problems_tags_dict:
                continue

            # 자카드 유사도 계산 (0부터 1 사이의 값)
            user_solved_tags = solved_problems_tags_dict.get(problem_index, set())
            problem_tags = first_filtered_problems_tags_dict[problem_index]
            if user_solved_tags:
                similarity = len(user_solved_tags.intersection(problem_tags)) / len(user_solved_tags.union(problem_tags))
            else:
                similarity = 0.0

            # 추천할 문제에 추가
            recommended_problems.append((problem_index, similarity))

        # 유사도가 높은 순으로 정렬
        recommended_problems.sort(key=lambda x: x[1], reverse=True)

        # 상위 n개의 문제를 추천
        n_recommendations = 50
        top_recommendations = recommended_problems[:n_recommendations]

        # 랜덤 30개 db에 저장
        random_recommendations = sample(top_recommendations, min(30,len(top_recommendations)))

        for problem_index, similarity in random_recommendations:
            problem = Problem.objects.get(problem_id=problem_index)
            recommend_problem = RecommendProblem(problem=problem, user=user_data, type=0)
            recommend_problem.save()

        # 2. 약점문제
        recommended_problems2 = []
        # 유사성 측정 및 추천
        for problem_index, _ in first_filtered_problems_tags_dict2.items():
            # 유저가 이미 푼 문제는 스킵
            if problem_index in solved_problems_tags_dict:
                continue

            # 자카드 유사도 계산 (0부터 1 사이의 값)
            user_solved_tags = solved_problems_tags_dict.get(problem_index, set())
            problem_tags = first_filtered_problems_tags_dict2[problem_index]
            if user_solved_tags:
                similarity = len(user_solved_tags.intersection(problem_tags)) / len(user_solved_tags.union(problem_tags))
            else:
                similarity = 0.0

            # 추천할 문제에 추가
            recommended_problems2.append((problem_index, similarity))

        # 유사도가 높은 순으로 정렬
        recommended_problems2.sort(key=lambda x: x[1], reverse=False)
        # print(recommended_problems2)
        # print('-------------------------------')

        # 상위 n개의 문제를 추천
        n_recommendations = 50
        top_recommendations2 = recommended_problems2[:n_recommendations]

        # 랜덤 30개 db에 저장
        random_recommendations2 = sample(top_recommendations2, min(30,len(top_recommendations2)))

        for problem_index, similarity in random_recommendations2:
            problem = Problem.objects.get(problem_id=problem_index)
            recommend_problem = RecommendProblem(problem=problem, user=user_data, type=1)
            recommend_problem.save()



        # 3. 비슷한유저문제
        input_bj_id = user.solved_ac_id
        # input_bj_id = 'smk921'
        #같은 클러스터 찾기
        matching_cluster = None
        for cluster_label, cluster_data in clusters.items():
            bj_ids_in_cluster = [item[1] for item in cluster_data]
            if input_bj_id in bj_ids_in_cluster:
                matching_cluster = cluster_data
                break
        # 유사한 다른 사용자의 solved_problem_history_id 가져오기
        similar_user_ids = [bj_id for bj_id in bj_ids_in_cluster if bj_id != input_bj_id]
        if len(similar_user_ids) < 1:
          pass
        else:
          similar_user_id = sample(similar_user_ids,1)
        # 중복되지 않는 problem 레코드 찾기
        # selected_user_id = User.objects.get(solved_ac_id = similar_user_id).user_id
        distinct_problem_ids = set()
        distinct_problems = SolvedProblemHistory.objects.filter(user__solved_ac_id=similar_user_id[0]).exclude(user__solved_ac_id=input_bj_id).values_list('problem__problem_id', flat=True)
        distinct_problem_ids.update(distinct_problems)

        filtered_problem_ids = []
        user_tier = bj_user_data.tier
        for problem_id in distinct_problem_ids:
            problem = Problem.objects.get(problem_id=problem_id)
            if problem.problem_level >= user_tier - 2:
                filtered_problem_ids.append(problem_id)

        # 무작위로 20개 문제 ID 선택
        relationship_rerecommended_problems = sample(filtered_problem_ids, min(20, len(filtered_problem_ids)))

        for relationship_rerecommended_problem in relationship_rerecommended_problems:
            recommend_problem = RecommendProblem(problem_id=relationship_rerecommended_problem, user_id=user_data.user_id, type = 2)
            recommend_problem.save()

    return Response({'result': "recommend_success"})


@api_view(['POST'])
def problem_correlation(request):
    # 중계 테이블에서 데이터를 가져옵니다.
    problem_tags = ProblemTag.objects.all()

    # Problem을 기준으로 Tag를 그룹화하기 위한 딕셔너리 생성
    problem_tag_dict = defaultdict(set)

    # 중계 테이블 데이터를 딕셔너리에 그룹화합니다.
    for problem_tag in problem_tags:
        problem_id = problem_tag.problem_id
        tag_id = problem_tag.tag_id
        problem_tag_dict[tag_id].add(problem_id)

    tag_lists = list(problem_tag_dict.values())
    # 각 태그 리스트 간의 상관 계수 계산
      # 각 태그의 등장 횟수 계산
    # problem_counts = defaultdict(int)
    # for problems in problem_tag_dict.values():
    #     for problem in problems:
    #         problem_counts[problem] += 1

    # print(problem_tag_dict[1])
    tag_correlations = []
    for key1, value1 in problem_tag_dict.items():
        for key2, value2 in problem_tag_dict.items():
            if key1 < key2:
                jaccard = jaccard_similarity(value1, value2)
                consine = cosine_similarity(value1, value2)
                tag_correlations.append((key1,key2,jaccard,consine))


    # 계산된 상관 계수를 데이터베이스에 저장
    for tag1, tag2, jaccard_correlation, consine_correlation in tag_correlations:
        tag_correlation = TagCorrelation(tag1_id=tag1, tag2_id=tag2, jaccard_correlation = jaccard_correlation, consine_correlation = consine_correlation)
        tag_correlation.save()

    for tag1, tag2, jaccard_correlation, consine_correlation in tag_correlations:
        tag_correlation = TagCorrelation(tag1_id=tag1, tag2_id=tag2, jaccard_correlation = jaccard_correlation, consine_correlation = consine_correlation)
        tag_correlation.save()    

    # 처리 결과를 응답으로 반환
    return Response({"message": "Tag correlations calculated and saved successfully."})


# 두 집합의 자카드 유사도 계산
def jaccard_similarity(set1, set2):
    intersection = len(set1.intersection(set2))
    union = len(set1.union(set2))
    similarity = intersection / union
    return similarity

# 두 집합의 코사인 유사도 계산
def cosine_similarity(set1, set2):
    intersection = len(set1.intersection(set2))
    norm1 = len(set1)
    norm2 = len(set2)
    similarity = intersection / (norm1 * norm2)
    return similarity


