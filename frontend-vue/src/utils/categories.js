export const violationCategoryOptions = ['暴力', '色情', '政治敏感', '其他违规']

export function splitCategories(value) {
  if (!value) {
    return []
  }
  return String(value)
    .split(/[,，;/；、\s]+/)
    .map((item) => item.trim())
    .filter(Boolean)
}

export function categoryTagType(category) {
  if (category === '暴力') {
    return 'danger'
  }
  if (category === '色情') {
    return 'warning'
  }
  if (category === '政治敏感') {
    return 'primary'
  }
  return 'info'
}

export function joinCategories(categories) {
  return splitCategories(categories.join(',')).join(',')
}

export function reviewThresholdText(category) {
  const thresholds = {
    教育科普: [45, 80],
    新闻资讯: [30, 85],
    商品广告: [20, 60],
    娱乐搞笑: [30, 65],
    生活记录: [30, 70],
    其他: [30, 70]
  }
  const [review, violation] = thresholds[category] || thresholds.其他
  return `复审 ${review} / 违规 ${violation}`
}
