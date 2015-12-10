package jp.ac.kcg.projectexercises.twitter.client


import jp.ac.kcg.projectexercises.twitter.client.event.OnBlockListener
import jp.ac.kcg.projectexercises.twitter.client.event.OnDeletionNoticeListener
import jp.ac.kcg.projectexercises.twitter.client.event.OnFavoriteListener
import jp.ac.kcg.projectexercises.twitter.client.event.OnFollowListener
import jp.ac.kcg.projectexercises.twitter.client.event.OnStatusListener
import jp.ac.kcg.projectexercises.twitter.client.event.OnUnBlockListener
import jp.ac.kcg.projectexercises.twitter.client.event.OnUnFavoriteListener
import jp.ac.kcg.projectexercises.twitter.client.event.OnUnFollowListener
import jp.ac.kcg.projectexercises.twitter.tweet.TweetFactory
import twitter4j.DirectMessage
import twitter4j.StallWarning
import twitter4j.Status
import twitter4j.StatusDeletionNotice
import twitter4j.User
import twitter4j.UserList
import twitter4j.UserStreamListener
import java.util.*

/**
 */
final class StreamImpl(val clientUser: ClientUser) : Stream, UserStreamListener {

    private val onStatusListeners = ArrayList<OnStatusListener>()
    private val onDeletionNoticeListener = ArrayList<OnDeletionNoticeListener>()
    private val onFavoriteListeners = ArrayList<OnFavoriteListener>()
    private val onUnFavoriteListener = ArrayList<OnUnFavoriteListener>()
    private val onFollowListeners = ArrayList<OnFollowListener>()
    private val onUnFollowListeners = ArrayList<OnUnFollowListener>()
    private val onBlockListeners = ArrayList<OnBlockListener>()
    private val onUnBlockListeners = ArrayList<OnUnBlockListener>()

    private val deletedStatusIds = ArrayList<Long>()

    override fun addOnStatusListener(listener: OnStatusListener): Stream {
        onStatusListeners.add(listener)
        return this
    }

    override fun addOnDeletionNoticeListener(listener: OnDeletionNoticeListener): Stream {
        onDeletionNoticeListener.add(listener)
        return this
    }

    override fun addOnFavoriteListener(listener: OnFavoriteListener): Stream {
        onFavoriteListeners.add(listener)
        return this
    }

    override fun addOnUnFavoriteListener(listener: OnUnFavoriteListener): Stream {
        onUnFavoriteListener.add(listener)
        return this
    }

    override fun addOnFollowListener(listener: OnFollowListener): Stream {
        onFollowListeners.add(listener)
        return this
    }

    override fun addOnUnFollowListener(listener: OnUnFollowListener): Stream {
        onUnFollowListeners.add(listener)
        return this
    }

    override fun addOnBlockListener(listener: OnBlockListener): Stream {
        onBlockListeners.add(listener)
        return this
    }

    override fun addOnUnBlockListener(listener: OnUnBlockListener): Stream {
        onUnBlockListeners.add(listener)
        return this
    }

    override fun removeOnStatusListener(listener: OnStatusListener): Stream {
        onStatusListeners.remove(listener)
        return this
    }

    override fun removeOnDeletionNoticeListener(listener: OnDeletionNoticeListener): Stream {
        onDeletionNoticeListener.remove(listener)
        return this
    }

    override fun removeOnFavoriteListener(listener: OnFavoriteListener): Stream {
        onFavoriteListeners.remove(listener)
        return this
    }

    override fun removeOnUnFavoriteListener(listener: OnUnFavoriteListener): Stream {
        onUnFavoriteListener.remove(listener)
        return this
    }

    override fun removeOnFollowListener(listener: OnFollowListener): Stream {
        onFollowListeners.remove(listener)
        return this
    }

    override fun removeOnUnFollowListener(listener: OnUnFavoriteListener): Stream {
        onUnFavoriteListener.remove(listener)
        return this
    }

    override fun removeOnBlockListener(listener: OnBlockListener): Stream {
        onBlockListeners.remove(listener)
        return this
    }

    override fun removeOnUnBlockListener(listener: OnUnBlockListener): Stream {
        onUnBlockListeners.remove(listener)
        return this
    }


    //以下UserStreamイベント
    override fun onDeletionNotice(directMessageId: Long, userId: Long) {
    }

    override fun onFriendList(friendIds: LongArray) {

    }

    override fun onFavorite(source: User, target: User, favoritedStatus: Status) {
        val tweet = TweetFactory.instance.createOrGetTweet(clientUser, favoritedStatus)
        onFavoriteListeners.forEach { it.onFavorite(source, target, tweet) }
    }

    override fun onUnfavorite(source: User, target: User, unfavoritedStatus: Status) {
        val tweet = TweetFactory.instance.createOrGetTweet(clientUser, unfavoritedStatus)
        onUnFavoriteListener.forEach { it.onUnFavorite(source, target, tweet) }
    }

    override fun onFollow(source: User, followedUser: User) {
        onFollowListeners.forEach { it.onFollow(source, followedUser) }
    }

    override fun onUnfollow(source: User, unfollowedUser: User) {
        onUnFollowListeners.forEach { it.onUnFollow(source, unfollowedUser) }
    }

    override fun onDirectMessage(directMessage: DirectMessage) {

    }

    override fun onUserListMemberAddition(addedMember: User, listOwner: User, list: UserList) {

    }

    override fun onUserListMemberDeletion(deletedMember: User, listOwner: User, list: UserList) {

    }

    override fun onUserListSubscription(subscriber: User, listOwner: User, list: UserList) {

    }

    override fun onUserListUnsubscription(subscriber: User, listOwner: User, list: UserList) {

    }

    override fun onUserListCreation(listOwner: User, list: UserList) {

    }

    override fun onUserListUpdate(listOwner: User, list: UserList) {

    }

    override fun onUserListDeletion(listOwner: User, list: UserList) {

    }

    override fun onUserProfileUpdate(updatedUser: User) {

    }

    override fun onUserSuspension(suspendedUser: Long) {

    }

    override fun onUserDeletion(deletedUser: Long) {

    }

    override fun onBlock(source: User, blockedUser: User) {
        onBlockListeners.forEach { it.onBlock(source, blockedUser) }
    }

    override fun onUnblock(source: User, unblockedUser: User) {
        onUnBlockListeners.forEach { it.onUnBlock(source, unblockedUser) }
    }

    override fun onRetweetedRetweet(source: User, target: User, retweetedStatus: Status) {

    }

    override fun onFavoritedRetweet(source: User, target: User, favoritedRetweeet: Status) {

    }

    override fun onQuotedTweet(source: User, target: User, quotingTweet: Status) {

    }

    override fun onStatus(status: Status) {
        if (!deletedStatusIds.any { it.equals(status.id) }) {
            val tweet = TweetFactory.instance.createOrGetTweet(clientUser, status)
            onStatusListeners.forEach { it.onStatus(tweet) }
        }
    }

    override fun onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {
        deletedStatusIds.add(statusDeletionNotice.statusId)
        onDeletionNoticeListener.forEach { it.onDeletionNotice(statusDeletionNotice) }
    }

    override fun onTrackLimitationNotice(numberOfLimitedStatuses: Int) {

    }

    override fun onScrubGeo(userId: Long, upToStatusId: Long) {

    }

    override fun onStallWarning(warning: StallWarning) {

    }

    override fun onException(ex: Exception) {

    }
}
